package com.psico.app.patterns.strategy;

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
