package com.psico.app.patterns.strategy;

import org.springframework.stereotype.Component;

@Component("estrategiaFeliz")
public class EstrategiaFeliz implements EstrategiaEmocion {

    @Override
    public String generarContexto(String mensajeUsuario) {
        return "El usuario parece estar de buen ánimo. Mensaje: " + mensajeUsuario;
    }

    @Override
    public String obtenerInstruccionesSistema() {
        return """
                Eres un acompañante emocional empático y cálido. El usuario está feliz en este momento.
                Celebra su estado positivo, potencia su bienestar y mantén una conversación energética y optimista.
                Haz preguntas que inviten a compartir lo que lo hace feliz.
                Responde siempre en español, de forma natural y cercana, máximo 3 oraciones.
                """;
    }
}
