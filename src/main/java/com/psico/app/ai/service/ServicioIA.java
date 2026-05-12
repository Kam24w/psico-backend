package com.psico.app.ai.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psico.app.ai.client.ClienteIA;
import com.psico.app.ai.model.AlertaSeguridad;
import com.psico.app.ai.repository.AlertaSeguridadRepository;
import com.psico.app.ai.repository.MemoriaContextoRepository;
import com.psico.app.ai.repository.PersonalidadIARepository;
import com.psico.app.auth.model.User;
import com.psico.app.emotion.model.TipoEmocion;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ServicioIA
 * Orquesta la generación de respuestas combinando:
 * 1. El mensaje del usuario
 * 2. La emoción detectada (vía Strategy + Factory)
 * 3. La petición a la API de Google Gemini/Gemma 4 (vía ClienteIA)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServicioIA {

    private final ClienteIA clienteIA;
    private final GeneradorRespuesta generadorRespuesta;
    private final AlertaSeguridadRepository alertaRepository;
    private final PersonalidadIARepository personalidadRepository;
    private final MemoriaContextoRepository memoriaRepository;
    private final EntityManager entityManager;

    public String generateResponse(Long userId, String userMessage, TipoEmocion emotion) {
        log.info("Generating response for user {} with emotion {}", userId, emotion);

        detectRisks(userId, userMessage);

        // 2. Obtener Personalidad Activa
        String personalidadPrompt = personalidadRepository.findByActivaTrue()
                .map(p -> p.getSystemPrompt())
                .orElse("Eres un asistente psicológico virtual altamente empático.");

        // 3. Obtener Memoria Reciente
        String memoryContext = memoriaRepository.findByUsuarioId(userId).stream()
                .limit(5)
                .map(m -> m.getClave() + ": " + m.getValor())
                .reduce("", (a, b) -> a + "\n" + b);

        String systemPrompt = generadorRespuesta.buildSystemPrompt(emotion, personalidadPrompt);
        String finalUserMessage = generadorRespuesta.buildUserMessage(userMessage, emotion, memoryContext);

        String rawResponse = clienteIA.enviarMensaje(systemPrompt, finalUserMessage);
        return cleanResponse(rawResponse);
    }

    @Deprecated(forRemoval = false)
    public String generarRespuesta(Long usuarioId, String mensajeUsuario, TipoEmocion emocion) {
        return generateResponse(usuarioId, mensajeUsuario, emocion);
    }

    private String cleanResponse(String texto) {
        if (texto == null || texto.isBlank()) return "Lo siento, no pude procesar tu mensaje.";
        
        String resultado = texto.trim();

        // 1. Estrategia principal para este modelo Gemma específico: 
        // Extraer el texto final que suele poner entre comillas cerca del final: * "Respuesta..." *
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(?s).*\\*\\s*\"([^\"]+)\"\\s*\\*.*").matcher(resultado);
        if (m.matches()) {
            return m.group(1).trim();
        }

        // 2. Estrategia de respaldo: Extraer si usa "Selected response:" o "Option X:" al final
        String lowerText = resultado.toLowerCase();
        if (lowerText.contains("selected response:")) {
            int idx = lowerText.lastIndexOf("selected response:");
            resultado = resultado.substring(idx + "selected response:".length()).trim();
        } else if (lowerText.contains("respuesta final:")) {
            int idx = lowerText.lastIndexOf("respuesta final:");
            resultado = resultado.substring(idx + "respuesta final:".length()).trim();
        }

        // 3. Limpiar por líneas para quitar el razonamiento que quede
        String[] lineas = resultado.split("\n");
        StringBuilder sb = new StringBuilder();
        
        for (String linea : lineas) {
            String t = linea.trim();
            if (t.isEmpty()) continue;
            
            // Filtros agresivos
            if (t.startsWith("* ") || t.startsWith("- ") || t.startsWith("**")) continue;
            if (t.toLowerCase().matches("^(user says|instruction \\d|el usuario dijo|goal:|constraints:|option \\d:).*")) continue;
            if (t.toLowerCase().matches("^(brief\\?|spanish\\?|step \\d|paso \\d).*")) continue;
            if (t.matches("^\\d+\\.\\s.*")) continue;
            if (t.matches("^\\[.*\\]$")) continue;
            
            sb.append(t).append(" ");
        }
        
        resultado = sb.toString().trim();

        // 3. Quitar asteriscos y comillas que puedan quedar atrapando el texto
        resultado = resultado.replaceAll("^\\*+|\\*+$", "").trim(); // Quitar * sueltos a los lados
        resultado = resultado.replaceAll("^\"|\"$", "").trim();     // Quitar comillas a los lados
        
        // Si por ser tan agresivos borramos todo, devolvemos un texto por defecto pero limpio
        if (resultado.isEmpty()) {
            resultado = texto.replaceAll("^\\*.*\\*\\s*", "").replaceAll("^\"|\"$", "").trim();
            if (resultado.isEmpty()) return "Hola, estoy aquí para escucharte.";
        }
        
        return resultado;
    }

    @Transactional
    public void detectRisks(Long userId, String message) {
        String lower = message.toLowerCase();

        int nivelRiesgo = 0;
        String tipo = null;

        // Nivel 10 — riesgo crítico: intención suicida directa
        if (lower.contains("suicidio") || lower.contains("quitarme la vida")
                || lower.contains("matarme") || lower.contains("no quiero vivir")
                || lower.contains("quiero morir") || lower.contains("acabar con mi vida")) {
            nivelRiesgo = 10;
            tipo = "SUICIDIO";
        }
        // Nivel 7 — autolesión
        else if (lower.contains("autolesion") || lower.contains("cortarme")
                || lower.contains("hacerme daño") || lower.contains("lastimarme")) {
            nivelRiesgo = 7;
            tipo = "AUTOLESION";
        }
        // Nivel 5 — desesperanza profunda
        else if (lower.contains("morir") || lower.contains("ya no puedo más")
                || lower.contains("no vale la pena vivir")) {
            nivelRiesgo = 5;
            tipo = "DESESPERANZA";
        }

        if (tipo != null) {
            // Referencia segura a entidad existente (sin cargar todos sus campos)
            User usuarioRef = entityManager.getReference(User.class, userId);

            // Truncar fragmento a 500 chars para evitar problemas de columna
            String fragmento = message.length() > 500 ? message.substring(0, 500) : message;

            AlertaSeguridad alerta = AlertaSeguridad.builder()
                    .usuario(usuarioRef)
                    .tipo(tipo)
                    .nivelRiesgo(nivelRiesgo)
                    .fragmentoDetectado(fragmento)
                    .build();
            alertaRepository.save(Objects.requireNonNull(alerta));

            log.warn("Security alert [{}] level {} detected for user {}", tipo, nivelRiesgo, userId);
        }
    }

    @Deprecated(forRemoval = false)
    @Transactional
    public void detectarRiesgos(Long usuarioId, String mensaje) {
        detectRisks(usuarioId, mensaje);
    }
}
