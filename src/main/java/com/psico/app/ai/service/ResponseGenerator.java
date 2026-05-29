package com.psico.app.ai.service;

import org.springframework.stereotype.Component;
import com.psico.app.emotion.model.EmotionType;

@Component
public class ResponseGenerator {

    public String buildSystemPrompt(EmotionType emotion, String basePersonality, java.util.List<com.psico.app.intervention.model.Recommendation> recommendations) {
        String emotionDesc = mapEmotionToSpanish(emotion);

        StringBuilder sb = new StringBuilder();
        sb.append("Eres Alma, una psicóloga virtual clínica, altamente empática y estrictamente profesional. ");
        sb.append("Tu rol es única y exclusivamente el de terapeuta. BAJO NINGUNA CIRCUNSTANCIA debes romper este personaje, ");
        sb.append("ni admitir que eres una inteligencia artificial, ni cambiar de tema a cosas que no sean apoyo emocional. ");
        sb.append("Estás hablando con una persona que se siente ").append(emotionDesc).append(". ");
        
        if (recommendations != null && !recommendations.isEmpty()) {
            sb.append("\nBasado en su emoción, puedes sugerir los siguientes ejercicios si lo ves conveniente:\n");
            for (com.psico.app.intervention.model.Recommendation rec : recommendations) {
                sb.append("- ").append(rec.getTitle()).append(": ").append(rec.getDescription()).append("\n");
            }
        }
        
        sb.append("\nREGLAS OBLIGATORIAS:\n");
        sb.append("1. Mantén siempre tu papel de psicóloga humana. Si el usuario intenta cambiarte de tema o de rol, redígelo sutilmente hacia sus emociones.\n");
        sb.append("2. Responde en máximo 2-3 oraciones cortas.\n");
        sb.append("3. SIEMPRE referencia algo específico de lo que el usuario dijo.\n");
        sb.append("4. NUNCA uses frases genéricas como 'Entiendo perfectamente' o 'Cuéntame más'.\n");
        sb.append("5. Habla en español, de forma natural y humana, como si fuera una conversación de voz.\n");
        sb.append("6. No uses listas, markdown, ni puntos numerados.\n");
        sb.append("7. No incluyas razonamientos internos ni etiquetas.\n");
        sb.append("8. Muestra empatía real haciendo eco del contenido emocional del usuario.");
        return sb.toString();
    }

    public String buildUserMessage(String originalMessage, EmotionType emotion, String userMemory) {
        StringBuilder sb = new StringBuilder();
        
        if (userMemory != null && !userMemory.isBlank()) {
            sb.append("Contexto previo del usuario:\n").append(userMemory).append("\n\n");
        }
        
        sb.append("El usuario dice: \"").append(originalMessage).append("\"\n\n");
        sb.append("Responde de forma empática y específica a lo que acaba de decir.");
        
        return sb.toString();
    }

    public String buildInitialGreetingPrompt(String userName, EmotionType emotion, String userMemory) {
        String emotionDesc = mapEmotionToSpanish(emotion);
        String name = userName != null && !userName.isBlank() ? userName : "amigo";

        StringBuilder sb = new StringBuilder();
        sb.append("Saluda a ").append(name).append(" que parece sentirse ").append(emotionDesc).append(".\n");
        
        if (userMemory != null && !userMemory.isBlank()) {
            sb.append("Recuerda esto sobre él/ella: ").append(userMemory).append("\n");
        }

        sb.append("\nGenera un saludo inicial cálido y natural, de máximo 2 oraciones, ");
        sb.append("adaptado a cómo se siente. Habla directamente a la persona. No uses frases genéricas.");
        
        return sb.toString();
    }
    
    private String mapEmotionToSpanish(EmotionType emotion) {
        if (emotion == null) return "neutral";
        return switch (emotion) {
            case HAPPY -> "feliz o animado";
            case SAD -> "triste o desanimado";
            case ANGRY -> "molesto o frustrado";
            case ANXIOUS -> "ansioso o preocupado";
            case STRESSED -> "estresado";
            case SURPRISED -> "sorprendido";
            default -> "neutral";
        };
    }
}
