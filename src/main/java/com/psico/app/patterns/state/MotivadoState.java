package com.psico.app.patterns.state;

import org.springframework.stereotype.Component;

@Component("motivadoState")
public class MotivadoState implements EstadoEmocional {

    @Override
    public String nombre() {
        return "MOTIVADO";
    }

    @Override
    public String obtenerInstruccionesSistema() {
        return "Fomenta la acción positiva y el refuerzo. Mantén un tono enérgico y constructivo.";
    }

    @Override
    public String ajustarRespuestaIA(String mensajeIA) {
        return "[Motiva] " + mensajeIA;
    }
}
