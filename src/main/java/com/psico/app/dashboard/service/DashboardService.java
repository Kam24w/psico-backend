package com.psico.app.dashboard.service;

import com.psico.app.analysis.model.AnalisisEmocional;
import com.psico.app.analysis.service.EmotionAnalysisService;
import com.psico.app.conversation.service.ConversationService;
import com.psico.app.memory.service.MemoryService;
import com.psico.app.risk.service.RiskService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ConversationService conversationService;
    private final RiskService riskService;
    private final MemoryService memoryService;

    public DashboardSummary obtenerResumen(Long usuarioId) {
        int conversacionesActivas = !conversationService.getActiveUserHistory(usuarioId).isEmpty() ? 1 : 0;
        int cantidadMemorias = memoryService.obtenerMemorias(usuarioId).size();
        // no hay repositorio directo para historico en este servicio, se deja como valor neutral

        return DashboardSummary.builder()
                .usuarioId(usuarioId)
                .conversacionesActivas(conversacionesActivas)
                .memoriasGuardadas(cantidadMemorias)
                .tendenciaEmocional(0.0)
                .ultimaAlerta(riskService.obtenerAlertas(usuarioId).stream()
                        .max(Comparator.comparing(a -> a.getCreadoEn()))
                        .map(a -> a.getRazon())
                        .orElse("Sin alertas recientes"))
                .build();
    }

    @Builder
    @Data
    public static class DashboardSummary {
        private Long usuarioId;
        private int conversacionesActivas;
        private int memoriasGuardadas;
        private double tendenciaEmocional;
        private String ultimaAlerta;
    }
}
