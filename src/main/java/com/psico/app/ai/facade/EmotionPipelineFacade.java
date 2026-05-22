package com.psico.app.ai.facade;

import org.springframework.stereotype.Component;

import com.psico.app.ai.service.ServicioIA;
import com.psico.app.analysis.service.EmotionAnalysisService;
import com.psico.app.conversation.model.Conversation;
import com.psico.app.conversation.model.Message;
import com.psico.app.conversation.service.ConversationService;
import com.psico.app.ai.dto.AiResponse;
import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.intervention.service.RecommendationService;
import com.psico.app.memory.service.MemoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmotionPipelineFacade {

    private final ServicioIA servicioIA;
    private final MemoryService memoryService;
    private final EmotionAnalysisService analysisService;
    private final RecommendationService recommendationService;
    private final ConversationService conversationService;
    private final com.psico.app.user.service.UserService userService;

    public Message ejecutarPipeline(Long usuarioId, String mensajeUsuario, TipoEmocion emocionDetectada) {
        return ejecutarPipeline(usuarioId, mensajeUsuario, emocionDetectada, "TEXTO");
    }

    public Message ejecutarPipeline(Long usuarioId, String mensajeUsuario, TipoEmocion emocionDetectada, String tipoSesion) {
        TipoEmocion emocionBase = emocionDetectada != null ? emocionDetectada : TipoEmocion.NEUTRAL;
        log.info("Pipeline emocional para usuario {} con emoción {} (sesión: {})", usuarioId, emocionBase, tipoSesion);

        // 1. Guardar mensaje del usuario y obtener conversación activa (filtrada por tipo)
        com.psico.app.auth.model.User user = userService.getById(usuarioId);
        Conversation conversation = conversationService.obtainAndStoreUserMessage(user, mensajeUsuario, emocionBase, tipoSesion);

        // 2. Guardar memoria y analizar conversación
        memoryService.guardarMemoria(usuarioId, mensajeUsuario, emocionBase);
        analysisService.analizarConversacion(usuarioId, conversationService.getActiveUserHistory(usuarioId, tipoSesion));

        // 3. Solicitar respuesta a la IA
        AiResponse aiResponse = servicioIA.generateResponse(usuarioId, mensajeUsuario, emocionBase);

        // 4. Obtener recomendaciones adicionales
        recommendationService.obtenerRecomendaciones(emocionBase);

        // 5. Guardar la respuesta de la IA y retornar
        return conversationService.storeAiResponse(conversation, aiResponse, emocionBase);
    }
}
