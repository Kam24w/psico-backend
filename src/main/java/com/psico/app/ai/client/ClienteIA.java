package com.psico.app.ai.client;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * ClienteIA — Groq API (OpenAI-compatible)
 *
 * Modelo híbrido:
 *   nivelRiesgo == 0  →  llama-3.1-8b-instant     (ultra rápido ~0.2s, voz fluida)
 *   nivelRiesgo  > 0  →  llama-3.3-70b-versatile  (más empático, momentos críticos)
 *
 * ServicioIA llama a enviarMensajeConRiesgo() cuando ya conoce el nivel de riesgo.
 * enviarMensaje() mantiene compatibilidad con generateInitialGreeting.
 */
@Component
public class ClienteIA {

    private static final Logger logger = LoggerFactory.getLogger(ClienteIA.class);

    private static final String MODEL_FAST = "llama-3.1-8b-instant";
    private static final String MODEL_SAFE = "llama-3.3-70b-versatile";

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Llamada sin nivel de riesgo — usa modelo rápido.
     * Mantiene compatibilidad con llamadas existentes en ServicioIA.
     */
    public String enviarMensaje(String systemPrompt, String userMessage) {
        return enviarMensajeConRiesgo(systemPrompt, userMessage, 0);
    }

    /**
     * Llamada con nivel de riesgo — elige el modelo según criticidad.
     * nivelRiesgo > 0 activa llama-3.3-70b para respuestas más empáticas.
     */
    public String enviarMensajeConRiesgo(String systemPrompt, String userMessage, int nivelRiesgo) {
        if (apiKey == null || apiKey.isBlank()) {
            logger.error("No se configuró GROQ_API_KEY. La IA no puede responder.");
            return "La IA no está configurada en este entorno. Falta GROQ_API_KEY.";
        }

        String modelo = nivelRiesgo > 0 ? MODEL_SAFE : MODEL_FAST;
        double temperature = nivelRiesgo > 0 ? 0.7 : 1.0;

        logger.info("=== GROQ API CALL | modelo: {} | nivelRiesgo: {} ===", modelo, nivelRiesgo);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // Formato OpenAI-compatible: system + user en el array messages
        Map<String, Object> body = Map.of(
            "model", modelo,
            "max_tokens", 512,
            "temperature", temperature,
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user",   "content", userMessage)
            )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(apiUrl, request, Object.class);

            logger.info("=== GROQ STATUS: {} ===", response.getStatusCode());
            logger.info("=== GROQ RAW BODY: {} ===", response.getBody());

            if (!(response.getBody() instanceof Map<?, ?> responseBody)) return fallback();

            // Estructura OpenAI: choices[0].message.content
            Object choicesObj = responseBody.get("choices");
            if (choicesObj instanceof List<?> choices && !choices.isEmpty()) {
                Object first = choices.get(0);
                if (first instanceof Map<?, ?> choice) {
                    Object msgObj = choice.get("message");
                    if (msgObj instanceof Map<?, ?> msg) {
                        Object textObj = msg.get("content");
                        if (textObj instanceof String text && !text.isBlank()) {
                            logger.info("=== GROQ RAW (passthrough) === \"{}\"", text);
                            return text;
                        }
                    }
                }
            }

        } catch (RestClientException e) {
            logger.error("Error al comunicarse con Groq: {}", e.getMessage());
            if (e instanceof org.springframework.web.client.HttpStatusCodeException httpEx) {
                logger.error("Detalle del error: {}", httpEx.getResponseBodyAsString());
            }
        }

        return fallback();
    }

    private String fallback() {
        return "Lo siento, hubo un problema al procesar tu mensaje. ¿Puedes intentarlo de nuevo?";
    }
}