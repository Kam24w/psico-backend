package com.psico.app.ai.facade;

import org.springframework.stereotype.Component;

import com.psico.app.ai.service.AIService;
import com.psico.app.analysis.service.EmotionAnalysisService;
import com.psico.app.conversation.model.Conversation;
import com.psico.app.conversation.model.Message;
import com.psico.app.conversation.service.ConversationService;
import com.psico.app.ai.dto.AiResponse;
import com.psico.app.emotion.model.EmotionType;
import com.psico.app.intervention.service.RecommendationService;
import com.psico.app.memory.service.MemoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmotionPipelineFacade {

    private final AIService aiService;
    private final MemoryService memoryService;
    private final EmotionAnalysisService analysisService;
    private final RecommendationService recommendationService;
    private final ConversationService conversationService;
    private final com.psico.app.user.service.UserService userService;

    public Message executePipeline(Long userId, String userMessage, EmotionType detectedEmotion) {
        return executePipeline(userId, userMessage, detectedEmotion, "TEXTO");
    }

    public Message executePipeline(Long userId, String userMessage, EmotionType detectedEmotion, String sessionType) {
        EmotionType baseEmotion = detectedEmotion != null ? detectedEmotion : EmotionType.NEUTRAL;
        log.info("Pipeline emocional para usuario {} con emoción {} (sesión: {})", userId, baseEmotion, sessionType);

        // 1. Guardar mensaje del usuario y obtener conversación activa (filtrada por tipo)
        com.psico.app.auth.model.User user = userService.getById(userId);
        Conversation conversation = conversationService.obtainAndStoreUserMessage(user, userMessage, baseEmotion, sessionType);

        // 2. Guardar memoria y analizar conversación
        memoryService.saveMemory(userId, userMessage, baseEmotion);
        analysisService.analyzeConversation(userId, conversationService.getActiveUserHistory(userId, sessionType));

        // 3. Obtener recomendaciones adicionales
        java.util.List<com.psico.app.intervention.model.Recommendation> recommendations = recommendationService.getRecommendations(baseEmotion);

        // 4. Solicitar respuesta a la IA
        AiResponse aiResponse = aiService.generateResponse(userId, userMessage, baseEmotion, recommendations);

        // 5. Guardar la respuesta de la IA y retornar
        return conversationService.storeAiResponse(conversation, aiResponse, baseEmotion);
    }
}
