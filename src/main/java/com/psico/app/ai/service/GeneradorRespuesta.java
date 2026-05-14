package com.psico.app.ai.service;

import org.springframework.stereotype.Component;
import com.psico.app.emotion.model.TipoEmocion;

@Component
public class GeneradorRespuesta {

    public String buildSystemPrompt(TipoEmocion emotion, String basePersonality) {
        String persona = basePersonality != null ? basePersonality : "Eres un asistente emocional empático y humano.";
        String emocion = emotion != null ? emotion.name() : "NEUTRAL";
        
        return String.format("%s. El usuario se siente %s. Responde en español de forma breve, natural y directa. NUNCA incluyas razonamientos internos, validaciones, borradores o etiquetas.", 
                persona, emocion);
    }

    public String buildUserMessage(String originalMessage, TipoEmocion emotion, String userMemory) {
        String contextualizedMessage = "El usuario dice: " + originalMessage;
        
        if (userMemory != null && !userMemory.isBlank()) {
            return String.format("Memoria relevante del usuario:\n%s\n\nMensaje actual:\n%s", 
                    userMemory, contextualizedMessage);
        }
        return contextualizedMessage;
    }

    public String buildInitialGreetingPrompt(String userName, TipoEmocion emotion, String userMemory) {
        String emotionDesc = emotion != null ? emotion.name() : "NEUTRAL";
        String prompt = String.format("El usuario se llama %s y hoy se siente %s.", 
                userName != null ? userName : "Usuario", emotionDesc);
        
        if (userMemory != null && !userMemory.isBlank()) {
            prompt += "\nRecuerda esto sobre él/ella:\n" + userMemory;
        }

        prompt += "\n\nInstrucción: Genera un saludo inicial muy corto y humano (máx 15 palabras) para una charla de voz. Sé empático y directo.";
        
        return prompt;
    }
}
