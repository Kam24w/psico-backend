package com.psico.app.ai.service;

import org.springframework.stereotype.Component;

import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.patterns.factory.FabricaEstrategia;
import com.psico.app.patterns.strategy.EstrategiaEmocion;

import lombok.RequiredArgsConstructor;

/**
 * GeneradorRespuesta
 * Combina el mensaje del usuario + emoción detectada
 * para construir el contexto completo que se enviará a Gemma 4.
 */
@Component
@RequiredArgsConstructor
public class GeneradorRespuesta {

    private final FabricaEstrategia fabricaEstrategia;

    public String buildSystemPrompt(TipoEmocion emotion, String basePersonality) {
        EstrategiaEmocion strategy = fabricaEstrategia.crear(emotion);
        String emotionInstructions = strategy.obtenerInstruccionesSistema();
        
        return String.format("%s\n\nContexto de Personalidad Adicional:\n%s\n\nREGLA ESTRICTA: Responde SOLO con el mensaje directo y empático para el usuario. NO incluyas tu proceso de pensamiento, NO uses asteriscos (*) para analizar el estado del usuario, y NO ofrezcas 'Option 1', 'Goal' ni viñetas internas. Dame únicamente la respuesta final limpia y natural.", 
                emotionInstructions,
                basePersonality != null ? basePersonality : "Eres un profesional empático.");
    }

    public String buildUserMessage(String originalMessage, TipoEmocion emotion, String userMemory) {
        EstrategiaEmocion strategy = fabricaEstrategia.crear(emotion);
        String contextualizedMessage = strategy.generarContexto(originalMessage);
        
        if (userMemory != null && !userMemory.isBlank()) {
            return String.format("Memoria relevante del usuario:\n%s\n\nMensaje actual:\n%s", 
                    userMemory, contextualizedMessage);
        }
        return contextualizedMessage;
    }

    @Deprecated(forRemoval = false)
    public String construirPromptSistema(TipoEmocion emocion, String personalidadBase) {
        return buildSystemPrompt(emocion, personalidadBase);
    }

    @Deprecated(forRemoval = false)
    public String construirMensajeUsuario(String mensajeOriginal, TipoEmocion emocion, String memoriaUsuario) {
        return buildUserMessage(mensajeOriginal, emocion, memoriaUsuario);
    }
}
