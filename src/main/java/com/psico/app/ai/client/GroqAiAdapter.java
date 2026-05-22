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
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * GroqAiAdapter — Adapter para la API de Groq (OpenAI-compatible)
 * Implementa el puerto ClienteIA.
 *
 * Modelo híbrido:
 *   nivelRiesgo == 0  →  llama-3.1-8b-instant     (ultra rápido ~0.2s, voz fluida)
 *   nivelRiesgo  > 0  →  llama-3.3-70b-versatile  (más empático, momentos críticos)
 */
@Component
@Primary
public class GroqAiAdapter implements AIClient {

    private static final Logger logger = LoggerFactory.getLogger(GroqAiAdapter.class);

    private static final String MODEL_FAST = "llama-3.1-8b-instant";
    private static final String MODEL_SAFE = "llama-3.3-70b-versatile";

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Call without risk level — uses fast model.
     * Maintains compatibility with existing calls.
     */
    @Override
    public String sendMessage(String systemPrompt, String userMessage) {
        return sendMessageWithRisk(systemPrompt, userMessage, 0);
    }

    /**
     * Call with risk level — chooses model according to criticality.
     * riskLevel > 0 activates llama-3.3-70b for more empathetic responses.
     */
    @Override
    public String sendMessageWithRisk(String systemPrompt, String userMessage, int riskLevel) {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("sin-configurar")) {
            logger.error("No se configuró GROQ_API_KEY. La IA no puede responder.");
            return "La IA no está configurada en este entorno. Falta GROQ_API_KEY.";
        }

        String modelo = riskLevel > 0 ? MODEL_SAFE : MODEL_FAST;
        double temperature = riskLevel > 0 ? 0.7 : 1.0;

        logger.info("=== GROQ API CALL | modelo: {} | riskLevel: {} ===", modelo, riskLevel);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // OpenAI-compatible format: system + user in messages array
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

            // OpenAI structure: choices[0].message.content
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