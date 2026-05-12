package com.psico.app.patterns.state;

public interface EstadoEmocional {
    String nombre();
    String obtenerInstruccionesSistema();
    String ajustarRespuestaIA(String mensajeIA);
}
