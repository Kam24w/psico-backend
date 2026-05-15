package com.psico.app.ai.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * GeminiAiAdapter — Adapter para la API de Gemini
 * Implementa el puerto ClienteIA como alternativa a Groq.
 * 
 * Por el momento sirve de ejemplo para el patrón Adapter.
 */
@Component
public class GeminiAiAdapter implements ClienteIA {

    private static final Logger logger = LoggerFactory.getLogger(GeminiAiAdapter.class);

    @Override
    public String enviarMensaje(String systemPrompt, String userMessage) {
        return enviarMensajeConRiesgo(systemPrompt, userMessage, 0);
    }

    @Override
    public String enviarMensajeConRiesgo(String systemPrompt, String userMessage, int nivelRiesgo) {
        logger.info("=== GEMINI API CALL (Mock) | nivelRiesgo: {} ===", nivelRiesgo);
        
        // Aquí iría la lógica real de llamada a la API de Google Gemini (Vertex AI / Generative Language API)
        // usando el RestTemplate o el cliente oficial de Java.
        
        return "Esta es una respuesta simulada desde el GeminiAiAdapter.";
    }
}
