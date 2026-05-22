package com.psico.app.ai.client;

/**
 * Port (Target) for the Adapter pattern in AI communication.
 * Allows AIService to be independent of specific implementations (Groq, Gemini, etc.).
 */
public interface AIClient {
    
    /**
     * Sends a message to the AI using the default configuration.
     * 
     * @param systemPrompt Context or personality of the AI.
     * @param userMessage User message.
     * @return AI Response.
     */
    String sendMessage(String systemPrompt, String userMessage);

    /**
     * Sends a message to the AI considering the detected risk level.
     * 
     * @param systemPrompt Context or personality of the AI.
     * @param userMessage User message.
     * @param riskLevel Detected risk level (0 to 10).
     * @return AI Response.
     */
    String sendMessageWithRisk(String systemPrompt, String userMessage, int riskLevel);
}
