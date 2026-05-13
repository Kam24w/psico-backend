package com.psico.app.analysis.controller;

import com.psico.app.analysis.model.AnalisisEmocional;
import com.psico.app.analysis.service.EmotionAnalysisService;
import com.psico.app.common.response.ApiResponse;
import com.psico.app.conversation.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analisis")
@RequiredArgsConstructor
public class EmotionAnalysisController {

    private final EmotionAnalysisService analysisService;
    private final ConversationService conversationService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponse<List<AnalisisEmocional>>> obtenerHistorico(@PathVariable Long usuarioId) {
        List<AnalisisEmocional> historico = conversationService.getUserConversations(usuarioId).stream()
                .map(conversacion -> analysisService.analizarConversacion(usuarioId, conversationService.getConversationHistory(conversacion.getId())))
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Histórico analizado", historico));
    }
}
