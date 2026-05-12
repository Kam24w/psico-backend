package com.psico.app.patterns.service;

import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.patterns.factory.FabricaEstrategia;
import com.psico.app.patterns.strategy.EstrategiaEmocion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatternService {

    private final FabricaEstrategia fabricaEstrategia;

    public PatternResult analizarTexto(String mensaje, TipoEmocion emocion) {
        EstrategiaEmocion estrategia = fabricaEstrategia.crear(emocion);
        String contexto = estrategia.generarContexto(mensaje);
        String instrucciones = estrategia.obtenerInstruccionesSistema();

        return PatternResult.builder()
                .emocion(emocion != null ? emocion.name() : "NEUTRAL")
                .contexto(contexto)
                .instrucciones(instrucciones)
                .build();
    }

    @lombok.Builder
    @lombok.Data
    public static class PatternResult {
        private String emocion;
        private String contexto;
        private String instrucciones;
    }
}
