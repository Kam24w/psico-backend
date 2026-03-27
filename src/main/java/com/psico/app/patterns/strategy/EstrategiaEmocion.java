package com.psico.app.patterns.strategy;

/**
 * PATRÓN STRATEGY
 * Define el comportamiento de respuesta según la emoción detectada.
 * Cada implementación adapta el tono y contexto del prompt enviado a Claude.
 */
public interface EstrategiaEmocion {
    /**
     * Genera el contexto emocional que se añadirá al prompt de la IA.
     * @param mensajeUsuario El mensaje original del usuario
     * @return Contexto enriquecido para la IA
     */
    String generarContexto(String mensajeUsuario);

    /**
     * Retorna instrucciones del sistema para la IA según la emoción.
     */
    String obtenerInstruccionesSistema();
}
