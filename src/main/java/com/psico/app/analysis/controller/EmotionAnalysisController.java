package com.psico.app.analysis.controller;

import com.psico.app.analysis.model.EmotionalAnalysis;
import com.psico.app.analysis.service.EmotionAnalysisService;
import com.psico.app.common.response.ApiResponse;
import com.psico.app.conversation.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/analysis", "/api/analisis"})
@RequiredArgsConstructor
public class EmotionAnalysisController {

    private final EmotionAnalysisService analysisService;
    private final ConversationService conversationService;

    @GetMapping({"/user/{userId}", "/usuario/{userId}"})
    public ResponseEntity<ApiResponse<List<EmotionalAnalysis>>> getHistory(@PathVariable Long userId) {
        List<EmotionalAnalysis> history = conversationService.getUserConversations(userId).stream()
                .map(conversation -> analysisService.analyzeConversation(userId, conversationService.getConversationHistory(conversation.getId())))
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Histórico analizado", history));
    }
}
