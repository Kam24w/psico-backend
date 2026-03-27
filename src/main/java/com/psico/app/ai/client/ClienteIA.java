package com.psico.app.ai.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * ClienteIA
 * Realiza la petición HTTP a la API de Google Gemini.
 */
@Component
@Slf4j
public class ClienteIA {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String enviarMensaje(String systemPrompt, String userMessage) {
        String urlConKey = apiUrl + "?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Gemini recibe system + user como partes del mismo mensaje
        String mensajeCompleto = systemPrompt + "\n\nUsuario: " + userMessage;

        Map<String, Object> body = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", mensajeCompleto)
                ))
            ),
            "generationConfig", Map.of(
                "maxOutputTokens", 512,
                "temperature", 0.8
            )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(urlConKey, request, Map.class);
            Map body2 = response.getBody();

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) body2.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    return (String) parts.get(0).get("text");
                }
            }
        } catch (Exception e) {
            log.error("Error al comunicarse con Gemini: {}", e.getMessage());
            return "Lo siento, hubo un problema al procesar tu mensaje. ¿Puedes intentarlo de nuevo?";
        }

        return "Lo siento, no pude procesar tu mensaje en este momento. ¿Puedes intentarlo de nuevo?";
    }
}
