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

    private String cleanResponse(String texto) {
        if (texto == null || texto.isBlank()) return "Lo siento, no pude procesar tu mensaje.";

        String resultado = texto.trim();

        // ── ESTRATEGIA 0 ─────────────────────────────────────────────────────────
        // Gemma a veces devuelve todo su razonamiento interno visible:
        // "* User Emotional State: ... * Goal: ... * Draft 1: ... * Draft 2: ..."
        // Detectamos si hay borradores (Draft) y extraemos el último.
        java.util.regex.Pattern pDraft = java.util.regex.Pattern.compile(
            "\\*\\s*Draft\\s*\\d+[:\\*]\\s*([^*]+?)(?=\\s*\\*\\s*(?:Draft|Spanish|$))",
            java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.DOTALL
        );
        java.util.regex.Matcher mDraft = pDraft.matcher(resultado);
        String ultimoDraft = null;
        while (mDraft.find()) {
            String candidato = mDraft.group(1).trim();
            if (candidato.length() > 10) ultimoDraft = candidato;
        }
        if (ultimoDraft != null) {
            return ultimoDraft.replaceAll("^[\"*]+|[\"*]+$", "").trim();
        }

        // Auto-evaluación tipo checklist: "* Spanish? Yes. * Max 3..."
        String primeraLinea = resultado.split("\n")[0].toLowerCase();
        boolean esChecklist = primeraLinea.matches(".*\\b(spanish|sentences|empathetic|direct|quotes|asterisks|user emotional|goal:|constraints).*");
        if (esChecklist) {
            return "Hola, estoy aquí para escucharte. ¿Cómo te sientes en este momento?";
        }


        // Gemma a veces razona en inglés y luego pone la respuesta entre comillas:
        // "Let's go with X. \"Respuesta.\" \"Respuesta duplicada.\""
        // Tomamos la ÚLTIMA cadena entre comillas dobles del texto — es la versión final.
        java.util.regex.Pattern pComillas = java.util.regex.Pattern.compile("\"([^\"]{10,})\"");
        java.util.regex.Matcher mComillas = pComillas.matcher(resultado);
        String ultimaComilla = null;
        while (mComillas.find()) {
            ultimaComilla = mComillas.group(1).trim();
        }
        if (ultimaComilla != null && !ultimaComilla.isBlank()) {
            return ultimaComilla;
        }

        // ── ESTRATEGIA 2 ─────────────────────────────────────────────────────────
        // Marcadores explícitos de respuesta final
        String lower = resultado.toLowerCase();
        String[] marcadores = {
            "selected response:", "respuesta final:", "respuesta:",
            "final response:", "my response:", "here is my response:"
        };
        for (String marcador : marcadores) {
            int idx = lower.lastIndexOf(marcador);
            if (idx >= 0) {
                String tras = resultado.substring(idx + marcador.length()).trim();
                tras = tras.replaceAll("^\"|\"$", "").trim();
                if (!tras.isBlank()) return tras;
            }
        }

        // ── ESTRATEGIA 3 ─────────────────────────────────────────────────────────
        // Filtrar línea a línea: descartar razonamiento en inglés y meta-comentarios
        String[] lineas = resultado.split("\n");
        StringBuilder sb = new StringBuilder();

        for (String linea : lineas) {
            String t = linea.trim();
            if (t.isEmpty()) continue;

            // Descartar razonamiento meta en inglés
            if (t.toLowerCase().matches("^(let'?s|okay|ok,|i('ll| will| need| should| want)|here'?s|now,|so,|alright|first,|the user|this is|i think|we need|my response|note:|step \\d|option \\d).*")) continue;
            // Descartar listas y bullets
            if (t.startsWith("* ") || t.startsWith("- ") || t.startsWith("**")) continue;
            if (t.matches("^\\d+\\.\\s.*")) continue;
            // Descartar etiquetas entre corchetes
            if (t.matches("^\\[.*\\]$")) continue;
            // Descartar líneas de instrucción/contexto en español
            if (t.toLowerCase().matches("^(goal:|constraints:|el usuario dijo|instrucción \\d|contexto:|tono:|brevedad:).*")) continue;

            sb.append(t).append(" ");
        }

        resultado = sb.toString().trim();
        // Quitar comillas y asteriscos sobrantes de los extremos
        resultado = resultado.replaceAll("^[\"*]+|[\"*]+$", "").trim();

        if (resultado.isEmpty()) return "Hola, estoy aquí para escucharte.";
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
}
