package com.psico.app.ai.service;

import org.springframework.stereotype.Component;
import com.psico.app.emotion.model.TipoEmocion;

@Component
public class GeneradorRespuesta {

    public String buildSystemPrompt(TipoEmocion emotion, String basePersonality) {
        String emocionDesc = mapEmotionToSpanish(emotion);

        return "Eres Alma, una psicóloga virtual empática y cercana. " +
               "Estás hablando por voz con una persona que se siente " + emocionDesc + ". " +
               "REGLAS OBLIGATORIAS:\n" +
               "1. Responde en máximo 2-3 oraciones cortas.\n" +
               "2. SIEMPRE referencia algo específico de lo que el usuario dijo.\n" +
               "3. NUNCA uses frases genéricas como 'Entiendo perfectamente' o 'Cuéntame más'.\n" +
               "4. Habla en español, de forma natural y humana, como si fuera una conversación.\n" +
               "5. No uses listas, markdown, ni puntos numerados.\n" +
               "6. No incluyas razonamientos internos ni etiquetas.\n" +
               "7. Muestra empatía real haciendo eco del contenido emocional del usuario.";
    }

    public String buildUserMessage(String originalMessage, TipoEmocion emotion, String userMemory) {
        StringBuilder sb = new StringBuilder();
        
        if (userMemory != null && !userMemory.isBlank()) {
            sb.append("Contexto previo del usuario:\n").append(userMemory).append("\n\n");
        }
        
        sb.append("El usuario dice: \"").append(originalMessage).append("\"\n\n");
        sb.append("Responde de forma empática y específica a lo que acaba de decir.");
        
        return sb.toString();
    }

    public String buildInitialGreetingPrompt(String userName, TipoEmocion emotion, String userMemory) {
        String emocionDesc = mapEmotionToSpanish(emotion);
        String nombre = userName != null && !userName.isBlank() ? userName : "amigo";

        StringBuilder sb = new StringBuilder();
        sb.append("Saluda a ").append(nombre).append(" que parece sentirse ").append(emocionDesc).append(".\n");
        
        if (userMemory != null && !userMemory.isBlank()) {
            sb.append("Recuerda esto sobre él/ella: ").append(userMemory).append("\n");
        }

        sb.append("\nGenera un saludo inicial cálido y natural, de máximo 2 oraciones, ");
        sb.append("adaptado a cómo se siente. Habla directamente a la persona. No uses frases genéricas.");
        
        return sb.toString();
    }
    
    private String mapEmotionToSpanish(TipoEmocion emotion) {
        if (emotion == null) return "neutral";
        return switch (emotion) {
            case FELIZ -> "feliz o animado";
            case TRISTE -> "triste o desanimado";
            case ENOJADO -> "molesto o frustrado";
            case ANSIOSO -> "ansioso o preocupado";
            case ESTRESADO -> "estresado";
            case SORPRENDIDO -> "sorprendido";
            default -> "neutral";
        };
    }
}
