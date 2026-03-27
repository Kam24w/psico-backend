package com.psico.app.patterns.strategy;

import org.springframework.stereotype.Component;

@Component("estrategiaEstresado")
public class EstrategiaEstresado implements EstrategiaEmocion {

    @Override
    public String generarContexto(String mensajeUsuario) {
        return "El usuario parece estar estresado o ansioso. Mensaje: " + mensajeUsuario;
    }

    @Override
    public String obtenerInstruccionesSistema() {
        return """
                Eres un acompañante emocional empático y calmante. El usuario está estresado o ansioso ahora mismo.
                Habla con voz tranquilizadora. Ayúdale a desacelerar, valida su estrés y sugiere respirar profundo si es apropiado.
                Usa frases cortas y claras. No lo abrumes con mucha información.
                Responde siempre en español, con calma y seguridad, máximo 3 oraciones.
                """;
    }
}
