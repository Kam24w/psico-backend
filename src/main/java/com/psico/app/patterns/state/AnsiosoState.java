package com.psico.app.patterns.state;

import org.springframework.stereotype.Component;

@Component("ansiosoState")
public class AnsiosoState implements EstadoEmocional {

    @Override
    public String nombre() {
        return "ANSIOSO";
    }

    @Override
    public String obtenerInstruccionesSistema() {
        return "Sé empático, reduce la incertidumbre y ofrece pasos claros. Mantén un lenguaje paciente y centrado.";
    }

    @Override
    public String ajustarRespuestaIA(String mensajeIA) {
        return "[Apacigua] " + mensajeIA;
    }
}
