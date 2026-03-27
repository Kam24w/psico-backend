package com.psico.app.patterns.strategy;

import org.springframework.stereotype.Component;

@Component("estrategiaTriste")
public class EstrategiaTriste implements EstrategiaEmocion {

    @Override
    public String generarContexto(String mensajeUsuario) {
        return "El usuario parece estar triste o bajo emocionalmente. Mensaje: " + mensajeUsuario;
    }

    @Override
    public String obtenerInstruccionesSistema() {
        return """
                Eres un acompañante emocional empático y comprensivo. El usuario está triste en este momento.
                Valida sus emociones sin minimizarlas. Muestra comprensión genuina y ofrece un espacio seguro.
                No des soluciones inmediatas; primero escucha y haz sentir al usuario comprendido.
                Responde siempre en español, con un tono suave y cálido, máximo 3 oraciones.
                """;
    }
}
