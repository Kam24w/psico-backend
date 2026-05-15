package com.psico.app.ai.client;

/**
 * Puerto (Target) para el patrón Adapter en la comunicación con IA.
 * Permite que ServicioIA no dependa de implementaciones específicas (Groq, Gemini, etc.).
 */
public interface ClienteIA {
    
    /**
     * Envía un mensaje a la IA usando la configuración por defecto.
     * 
     * @param systemPrompt Contexto o personalidad de la IA.
     * @param userMessage Mensaje del usuario.
     * @return Respuesta de la IA.
     */
    String enviarMensaje(String systemPrompt, String userMessage);

    /**
     * Envía un mensaje a la IA considerando el nivel de riesgo detectado.
     * 
     * @param systemPrompt Contexto o personalidad de la IA.
     * @param userMessage Mensaje del usuario.
     * @param nivelRiesgo Nivel de riesgo detectado (0 a 10).
     * @return Respuesta de la IA.
     */
    String enviarMensajeConRiesgo(String systemPrompt, String userMessage, int nivelRiesgo);
}
