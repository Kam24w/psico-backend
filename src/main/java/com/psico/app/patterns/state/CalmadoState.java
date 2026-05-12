package com.psico.app.patterns.state;

import org.springframework.stereotype.Component;

@Component("calmadoState")
public class CalmadoState implements EstadoEmocional {

    @Override
    public String nombre() {
        return "CALMADO";
    }

    @Override
    public String obtenerInstruccionesSistema() {
        return "Actúa como un acompañante suave y reflexivo. Prioriza la tranquilidad y la estabilidad emocional.";
    }

    @Override
    public String ajustarRespuestaIA(String mensajeIA) {
        return "[Calma] " + mensajeIA;
    }
}
