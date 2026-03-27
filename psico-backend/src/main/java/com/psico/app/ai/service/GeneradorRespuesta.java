package com.psico.app.ai.service;

import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.patterns.factory.FabricaEstrategia;
import com.psico.app.patterns.strategy.EstrategiaEmocion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * GeneradorRespuesta
 * Combina el mensaje del usuario + emoción detectada
 * para construir el contexto completo que se enviará a Claude.
 */
@Component
@RequiredArgsConstructor
public class GeneradorRespuesta {

    private final FabricaEstrategia fabricaEstrategia;

    public String construirPromptSistema(TipoEmocion emocion) {
        EstrategiaEmocion estrategia = fabricaEstrategia.crear(emocion);
        return estrategia.obtenerInstruccionesSistema();
    }

    public String construirMensajeUsuario(String mensajeOriginal, TipoEmocion emocion) {
        EstrategiaEmocion estrategia = fabricaEstrategia.crear(emocion);
        return estrategia.generarContexto(mensajeOriginal);
    }
}
