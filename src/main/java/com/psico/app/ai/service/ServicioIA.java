package com.psico.app.ai.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psico.app.ai.client.ClienteIA;
import com.psico.app.ai.model.AlertaSeguridad;
import com.psico.app.ai.repository.AlertaSeguridadRepository;
import com.psico.app.ai.repository.MemoriaContextoRepository;
import com.psico.app.ai.repository.PersonalidadIARepository;
import com.psico.app.auth.model.Usuario;
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

    public String generarRespuesta(Long usuarioId, String mensajeUsuario, TipoEmocion emocion) {
        log.info("Generando respuesta para usuario {} con emoción: {}", usuarioId, emocion);

        // 1. Verificar Seguridad (Risk Detection)
        detectarRiesgos(usuarioId, mensajeUsuario);

        // 2. Obtener Personalidad Activa
        String personalidadPrompt = personalidadRepository.findByActivaTrue()
                .map(p -> p.getSystemPrompt())
                .orElse("Eres un asistente psicológico virtual altamente empático.");

        // 3. Obtener Memoria Reciente
        String memoriaContexto = memoriaRepository.findByUsuarioId(usuarioId).stream()
                .limit(5)
                .map(m -> m.getClave() + ": " + m.getValor())
                .reduce("", (a, b) -> a + "\n" + b);

        // 4. Construir Prompts
        String systemPrompt = generadorRespuesta.construirPromptSistema(emocion, personalidadPrompt);
        String userMessage  = generadorRespuesta.construirMensajeUsuario(mensajeUsuario, emocion, memoriaContexto);

        // 5. Llamar a Gemma 4 y limpiar la respuesta
        String respuestaCruda = clienteIA.enviarMensaje(systemPrompt, userMessage);
        return limpiarRespuesta(respuestaCruda);
    }

    private String limpiarRespuesta(String texto) {
        if (texto == null || texto.isBlank()) return "Lo siento, no pude procesar tu mensaje.";
        
        String resultado = texto.trim();

        // 1. Extraer del JSON forzado {"respuesta": "..."}
        java.util.regex.Matcher jsonMatcher = java.util.regex.Pattern.compile("(?is).*\"respuesta\"\\s*:\\s*\"(.*?)\".*").matcher(resultado);
        if (jsonMatcher.matches()) {
            return jsonMatcher.group(1).trim();
        }

        // 2. Si la IA desobedece el JSON y escupe "Let's go with..." y luego pone comillas
        // Busca el texto más largo dentro de las últimas comillas
        java.util.regex.Matcher quotesMatcher = java.util.regex.Pattern.compile("(?s).*\"([^\"]+)\"\\s*[^\"a-zA-Z]*$").matcher(resultado);
        if (quotesMatcher.matches()) {
            return quotesMatcher.group(1).trim();
        }
        
        // 3. Fallback: Limpieza muy agresiva por si falla lo anterior
        String[] lineas = resultado.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String linea : lineas) {
            String t = linea.trim();
            if (t.isEmpty()) continue;
            if (t.startsWith("* ") || t.startsWith("- ") || t.startsWith("**")) continue;
            if (t.toLowerCase().matches("^(user says|instruction|goal:|option|let's go with).*")) continue;
            sb.append(t).append(" ");
        }
        
        resultado = sb.toString().trim();
        resultado = resultado.replaceAll("^\\*+|\\*+$", "").replaceAll("^\"|\"$", "").trim();
        
        return resultado.isEmpty() ? "Hola, estoy aquí para escucharte." : resultado;
    }

    @Transactional
    public void detectarRiesgos(Long usuarioId, String mensaje) {
        String lower = mensaje.toLowerCase();

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
            Usuario usuarioRef = entityManager.getReference(Usuario.class, usuarioId);

            // Truncar fragmento a 500 chars para evitar problemas de columna
            String fragmento = mensaje.length() > 500 ? mensaje.substring(0, 500) : mensaje;

            AlertaSeguridad alerta = AlertaSeguridad.builder()
                    .usuario(usuarioRef)
                    .tipo(tipo)
                    .nivelRiesgo(nivelRiesgo)
                    .fragmentoDetectado(fragmento)
                    .build();
            alertaRepository.save(Objects.requireNonNull(alerta));

            log.warn("¡ALERTA DE SEGURIDAD [{}] nivel {} detectada para usuario {}!", tipo, nivelRiesgo, usuarioId);
        }
    }
}
