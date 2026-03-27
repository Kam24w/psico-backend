package com.psico.app.patterns.strategy;

import org.springframework.stereotype.Component;

@Component("estrategiaNeutral")
public class EstrategiaNeutral implements EstrategiaEmocion {

    @Override
    public String generarContexto(String mensajeUsuario) {
        return "El usuario está en un estado emocional neutral. Mensaje: " + mensajeUsuario;
    }

    @Override
    public String obtenerInstruccionesSistema() {
        return """
                Eres un acompañante emocional empático e inteligente. El usuario está en un estado neutral.
                Mantén una conversación abierta, acogedora y natural. Muestra interés genuino por cómo se siente.
                Responde siempre en español, de forma amigable y cercana, máximo 3 oraciones.
                """;
    }
}
