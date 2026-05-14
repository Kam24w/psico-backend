package com.psico.app.ai.service;

import org.springframework.stereotype.Component;
import com.psico.app.emotion.model.TipoEmocion;

@Component
public class GeneradorRespuesta {

    public String buildSystemPrompt(TipoEmocion emotion, String basePersonality) {
        String emotionInstructions = "El usuario está experimentando: " + (emotion != null ? emotion.name() : "NEUTRAL");
        
        return String.format("%s\n\nContexto de Personalidad Adicional:\n%s\n\nREGLA ESTRICTA: Responde SOLO con el mensaje directo y empático para el usuario. NO incluyas tu proceso de pensamiento, NO uses asteriscos (*) para analizar el estado del usuario, y NO ofrezcas 'Option 1', 'Goal' ni viñetas internas. Dame únicamente la respuesta final limpia y natural.", 
                emotionInstructions,
                basePersonality != null ? basePersonality : "Eres un profesional empático.");
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

        prompt += "\n\nREGLA: Genera un saludo INICIAL muy corto, empático y natural (máximo 15 palabras) para iniciar una conversación por voz. Pregúntale cómo está o comenta algo sobre su estado actual de forma sutil.";
        
        return prompt;
    }
}
