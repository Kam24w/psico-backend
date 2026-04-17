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
 * ClienteIA
 * Realiza la petición HTTP a la API de Google Gemini usando Gemma 4.
 */
@Component
public class ClienteIA {

    private static final Logger logger = LoggerFactory.getLogger(ClienteIA.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String enviarMensaje(String systemPrompt, String userMessage) {
        if (apiKey == null || apiKey.isBlank()) {
            logger.error("No se configuró GEMINI_API_KEY. La IA no puede responder.");
            return "La IA no está configurada en este entorno. Falta GEMINI_API_KEY.";
        }

        String urlConKey = apiUrl + "?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
            "systemInstruction", Map.of(
                "parts", List.of(
                    Map.of("text", systemPrompt)
                )
            ),
            "contents", List.of(
                Map.of(
                    "role", "user",
                    "parts", List.of(
                        Map.of("text", userMessage)
                    )
                ))
            ),
            "generationConfig", Map.of(
                "maxOutputTokens", 512,
                "temperature", 0.8
            )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(
                    urlConKey,
                    request,
                    Object.class
            );

            Object responseBodyObj = response.getBody();
            if (!(responseBodyObj instanceof Map<?, ?> responseBody)) {
                return "Lo siento, no pude procesar tu mensaje en este momento. ¿Puedes intentarlo de nuevo?";
            }

            Object candidatesObj = responseBody.get("candidates");
            if (candidatesObj instanceof List<?> candidates && !candidates.isEmpty()) {
                Object firstCandidateObj = candidates.get(0);
                if (firstCandidateObj instanceof Map<?, ?> firstCandidate) {
                    Object contentObj = firstCandidate.get("content");
                    if (contentObj instanceof Map<?, ?> content) {
                        Object partsObj = content.get("parts");
                        if (partsObj instanceof List<?> parts && !parts.isEmpty()) {
                            Object firstPartObj = parts.get(0);
                            if (firstPartObj instanceof Map<?, ?> firstPart) {
                                Object textObj = firstPart.get("text");
                                if (textObj instanceof String text && !text.isBlank()) {
                                    return text;
                                }
                            }
                        }
                    }
                }
            }
        } catch (RestClientException e) {
            logger.error("Error al comunicarse con Gemma/Gemini: {}", e.getMessage(), e);
            return "Lo siento, hubo un problema al procesar tu mensaje. ¿Puedes intentarlo de nuevo?";
        }

        return "Lo siento, no pude procesar tu mensaje en este momento. ¿Puedes intentarlo de nuevo?";
    }
}
