package com.psico.app.ai.facade;

import org.springframework.stereotype.Component;

import com.psico.app.ai.adapter.ProveedorIA;
import com.psico.app.ai.proxy.IAServiceProxy;
import com.psico.app.analysis.service.EmotionAnalysisService;
import com.psico.app.conversation.service.ConversationService;
import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.intervention.service.RecommendationService;
import com.psico.app.memory.service.MemoryService;
import com.psico.app.patterns.factory.FabricaEstrategia;
import com.psico.app.patterns.observer.DetectorEmocion;
import com.psico.app.patterns.state.UsuarioEstadoContext;
import com.psico.app.risk.model.NivelRiesgo;
import com.psico.app.risk.service.RiskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmotionPipelineFacade {

    private final IAServiceProxy iaProxy;
    private final UsuarioEstadoContext estadoContext;
    private final FabricaEstrategia fabricaEstrategia;
    private final DetectorEmocion detectorEmocion;
    private final MemoryService memoryService;
    private final RiskService riskService;
    private final EmotionAnalysisService analysisService;
    private final RecommendationService recommendationService;
    private final ConversationService conversationService;

    public String ejecutarPipeline(Long usuarioId, String mensajeUsuario, TipoEmocion emocionDetectada) {
        TipoEmocion emocionBase = emocionDetectada != null ? emocionDetectada : detectorEmocion.getEstadoActual(usuarioId);
        log.info("Pipeline emocional para usuario {} con emoción {}", usuarioId, emocionBase);

        var estado = estadoContext.obtenerEstado(emocionBase);
        var estrategia = fabricaEstrategia.crear(emocionBase);
        var contexto = estrategia.generarContexto(mensajeUsuario);
        var instrucciones = estado.obtenerInstruccionesSistema();
        var systemPrompt = instrucciones + "\n" + contexto;

        NivelRiesgo nivelRiesgo = riskService.evaluarRiesgo(usuarioId, emocionBase, mensajeUsuario);
        if (nivelRiesgo != NivelRiesgo.BAJO) {
            riskService.registrarAlerta(usuarioId, nivelRiesgo, "Riesgo detectado durante procesamiento de mensaje");
        }

        memoryService.guardarMemoria(usuarioId, mensajeUsuario, emocionBase);
        analysisService.analizarConversacion(usuarioId, conversationService.getActiveUserHistory(usuarioId));

        String respuestaIA = iaProxy.generarRespuesta(ProveedorIA.GEMINI, systemPrompt, mensajeUsuario);
        String respuestaDecorada = estado.ajustarRespuestaIA(respuestaIA);

        recommendationService.obtenerRecomendaciones(emocionBase);
        return respuestaDecorada;
    }
}
