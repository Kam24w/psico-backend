package com.psico.app.ai.facade;

import org.springframework.stereotype.Component;

import com.psico.app.ai.client.ClienteIA;
import com.psico.app.analysis.service.EmotionAnalysisService;
import com.psico.app.conversation.service.ConversationService;
import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.intervention.service.RecommendationService;
import com.psico.app.memory.service.MemoryService;
import com.psico.app.risk.model.NivelRiesgo;
import com.psico.app.risk.service.RiskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmotionPipelineFacade {

    private final ClienteIA clienteIA;
    private final MemoryService memoryService;
    private final RiskService riskService;
    private final EmotionAnalysisService analysisService;
    private final RecommendationService recommendationService;
    private final ConversationService conversationService;

    public String ejecutarPipeline(Long usuarioId, String mensajeUsuario, TipoEmocion emocionDetectada) {
        TipoEmocion emocionBase = emocionDetectada != null ? emocionDetectada : TipoEmocion.NEUTRAL;
        log.info("Pipeline emocional para usuario {} con emoción {}", usuarioId, emocionBase);

        String systemPrompt = "Actúa como un terapeuta compasivo. El usuario está sintiendo: " + emocionBase.name() + ".";

        NivelRiesgo nivelRiesgo = riskService.evaluarRiesgo(usuarioId, emocionBase, mensajeUsuario);
        if (nivelRiesgo != NivelRiesgo.BAJO) {
            riskService.registrarAlerta(usuarioId, nivelRiesgo, "Riesgo detectado durante procesamiento de mensaje");
        }

        memoryService.guardarMemoria(usuarioId, mensajeUsuario, emocionBase);
        analysisService.analizarConversacion(usuarioId, conversationService.getActiveUserHistory(usuarioId));

        String respuestaIA = clienteIA.enviarMensaje(systemPrompt, mensajeUsuario);

        recommendationService.obtenerRecomendaciones(emocionBase);
        return respuestaIA;
    }
}
