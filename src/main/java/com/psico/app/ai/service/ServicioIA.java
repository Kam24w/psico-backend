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

        String personalidadPrompt = personalidadRepository.findByActivaTrue()
                .map(p -> p.getSystemPrompt())
                .orElse("Eres un asistente emocional empático.");

        String memoryContext = getRecentMemory(userId);

        String systemPrompt = generadorRespuesta.buildSystemPrompt(emotion, personalidadPrompt);
        String finalUserMessage = generadorRespuesta.buildUserMessage(userMessage, emotion, memoryContext);

        String rawResponse = clienteIA.enviarMensaje(systemPrompt, finalUserMessage);
        return cleanResponse(rawResponse);
    }

    private String cleanResponse(String texto) {
        if (texto == null || texto.isBlank()) return "Hola, estoy aquí para escucharte.";

        // 1. Detección de contenido interno (Draft, Goal, Option, etc.)
        String[] forbiddenTokens = {
            "Constraint", "Goal:", "Option", "Draft", "Reasoning", 
            "Validation", "Analysis", "Plan:", "Step 1", "Task:", 
            "Self-correction", "User status:", "Selected response:"
        };

        for (String token : forbiddenTokens) {
            if (texto.contains(token)) {
                log.warn("Internal AI content detected! Falling back. Token found: {}", token);
                return "Entiendo perfectamente lo que dices. ¿Me podrías contar un poco más sobre eso?";
            }
        }

        // 2. Limpieza básica
        String resultado = texto.trim();
        resultado = resultado.replaceAll("^\\*.*\\*\\s*", ""); // Quitar pensamientos entre asteriscos
        resultado = resultado.replaceAll("^\"|\"$", "").trim(); // Quitar comillas envolventes
        
        // Si tiene markdown de lista, Gemma está razonando o ignorando la instrucción de brevedad
        if (resultado.contains("* ") || resultado.contains("- ") || resultado.matches("(?s).*\\d\\..*")) {
             log.warn("Markdown list detected in response, returning fallback.");
             return "Entiendo. ¿Te gustaría profundizar un poco más en eso?";
        }

        if (resultado.isEmpty()) return "Entiendo perfectamente.";
        
        return resultado;
    }

    @Transactional
    public void detectRisks(Long userId, String message) {
        if (message == null) return;
        String lower = message.toLowerCase();

        int nivelRiesgo = 0;
        String tipo = null;

        if (lower.contains("suicidio") || lower.contains("quitarme la vida")
                || lower.contains("matarme") || lower.contains("no quiero vivir")
                || lower.contains("quiero morir") || lower.contains("acabar con mi vida")) {
            nivelRiesgo = 10;
            tipo = "SUICIDIO";
        } else if (lower.contains("autolesion") || lower.contains("cortarme")
                || lower.contains("hacerme daño") || lower.contains("lastimarme")) {
            nivelRiesgo = 7;
            tipo = "AUTOLESION";
        } else if (lower.contains("morir") || lower.contains("ya no puedo más")
                || lower.contains("no vale la pena vivir")) {
            nivelRiesgo = 5;
            tipo = "DESESPERANZA";
        }

        if (tipo != null) {
            User usuarioRef = entityManager.getReference(User.class, userId);
            String fragmento = message.length() > 500 ? message.substring(0, 500) : message;

            AlertaSeguridad alerta = AlertaSeguridad.builder()
                    .usuario(usuarioRef)
                    .tipo(tipo)
                    .nivelRiesgo(nivelRiesgo)
                    .fragmentoDetectado(fragmento)
                    .build();
            alertaRepository.save(alerta);
            log.warn("Security alert [{}] level {} detected for user {}", tipo, nivelRiesgo, userId);
        }
    }

    public String generateInitialGreeting(Long userId, String userName, TipoEmocion emotion) {
        log.info("Generating initial greeting for user {} ({}) with emotion {}", userId, userName, emotion);

        String memoryContext = getRecentMemory(userId);
        String systemPrompt = "Eres un asistente psicológico empático iniciando una sesión de voz.";
        String userMessage = generadorRespuesta.buildInitialGreetingPrompt(userName, emotion, memoryContext);

        String rawResponse = clienteIA.enviarMensaje(systemPrompt, userMessage);
        return cleanResponse(rawResponse);
    }

    private String getRecentMemory(Long userId) {
        return memoriaRepository.findByUsuarioId(userId).stream()
                .limit(5)
                .map(m -> m.getClave() + ": " + m.getValor())
                .reduce("", (a, b) -> a + "\n" + b);
    }
}
