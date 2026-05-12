package com.psico.app.patterns.state;

import org.springframework.stereotype.Component;

@Component("crisisState")
public class CrisisState implements EstadoEmocional {

    @Override
    public String nombre() {
        return "CRISIS";
    }

    @Override
    public String obtenerInstruccionesSistema() {
        return "Prioriza la seguridad y la contención inmediata. Proporciona apoyo emocional directo y sugiere pasos concretos para manejar la crisis.";
    }

    @Override
    public String ajustarRespuestaIA(String mensajeIA) {
        return "[Contención] " + mensajeIA;
    }
}
