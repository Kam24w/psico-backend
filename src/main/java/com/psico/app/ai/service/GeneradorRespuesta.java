package com.psico.app.ai.service;

import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.patterns.factory.FabricaEstrategia;
import com.psico.app.patterns.strategy.EstrategiaEmocion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * GeneradorRespuesta
 * Combina el mensaje del usuario + emoción detectada
 * para construir el contexto completo que se enviará a Gemma 4.
 */
@Component
@RequiredArgsConstructor
public class GeneradorRespuesta {

    private final FabricaEstrategia fabricaEstrategia;

    public String construirPromptSistema(TipoEmocion emocion, String personalidadBase) {
        EstrategiaEmocion estrategia = fabricaEstrategia.crear(emocion);
        String instruccionesEmocion = estrategia.obtenerInstruccionesSistema();
        
        return String.format("%s\n\nContexto de Personalidad Adicional:\n%s\n\nREGLA ESTRICTA: Responde SOLO con el mensaje directo y empático para el usuario. NO incluyas tu proceso de pensamiento, NO uses asteriscos (*) para analizar el estado del usuario, y NO ofrezcas 'Option 1', 'Goal' ni viñetas internas. Dame únicamente la respuesta final limpia y natural.", 
                instruccionesEmocion, 
                personalidadBase != null ? personalidadBase : "Eres un profesional empático.");
    }

    public String construirMensajeUsuario(String mensajeOriginal, TipoEmocion emocion, String memoriaUsuario) {
        EstrategiaEmocion estrategia = fabricaEstrategia.crear(emocion);
        String mensajeContextualizado = estrategia.generarContexto(mensajeOriginal);
        
        if (memoriaUsuario != null && !memoriaUsuario.isBlank()) {
            return String.format("Memoria relevante del usuario:\n%s\n\nMensaje actual:\n%s", 
                    memoriaUsuario, mensajeContextualizado);
        }
        return mensajeContextualizado;
    }
}
