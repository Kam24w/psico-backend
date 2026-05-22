package com.psico.app.dashboard.service;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.psico.app.conversation.service.ConversationService;
import com.psico.app.memory.service.MemoryService;
import com.psico.app.risk.service.RiskService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ConversationService conversationService;
    private final RiskService riskService;
    private final MemoryService memoryService;

    public DashboardSummary getSummary(Long userId) {
        int activeConversations = !conversationService.getActiveUserHistory(userId).isEmpty() ? 1 : 0;
        int savedMemories = memoryService.getMemories(userId).size();

        return DashboardSummary.builder()
                .userId(userId)
                .activeConversations(activeConversations)
                .savedMemories(savedMemories)
                .emotionalTrend(0.0)
                .latestAlert(riskService.getAlerts(userId).stream()
                        .max(Comparator.comparing(a -> a.getCreatedAt()))
                        .map(a -> a.getReason())
                        .orElse("Sin alertas recientes"))
                .build();
    }

    @Builder
    @Data
    public static class DashboardSummary {
        @JsonProperty("userId")
        @JsonAlias("usuarioId")
        private Long userId;

        @JsonProperty("activeConversations")
        @JsonAlias("conversacionesActivas")
        private int activeConversations;

        @JsonProperty("savedMemories")
        @JsonAlias("memoriasGuardadas")
        private int savedMemories;

        @JsonProperty("emotionalTrend")
        @JsonAlias("tendenciaEmocional")
        private double emotionalTrend;

        @JsonProperty("latestAlert")
        @JsonAlias("ultimaAlerta")
        private String latestAlert;
    }
}
