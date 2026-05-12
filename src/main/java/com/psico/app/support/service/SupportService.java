package com.psico.app.support.service;

import com.psico.app.risk.model.NivelRiesgo;
import com.psico.app.risk.service.RiskService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupportService {

    public SupportTicket escalarCaso(Long usuarioId, String razon, NivelRiesgo nivelRiesgo) {
        return SupportTicket.builder()
                .usuarioId(usuarioId)
                .nivelRiesgo(nivelRiesgo)
                .razon(razon)
                .estado("PENDIENTE")
                .build();
    }

    @Builder
    @Data
    public static class SupportTicket {
        private Long usuarioId;
        private NivelRiesgo nivelRiesgo;
        private String razon;
        private String estado;
    }
}
