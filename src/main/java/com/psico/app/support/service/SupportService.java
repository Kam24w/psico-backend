package com.psico.app.support.service;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.psico.app.risk.model.RiskLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupportService {

    public SupportTicket escalateCase(Long userId, String reason, RiskLevel riskLevel) {
        return SupportTicket.builder()
                .userId(userId)
                .riskLevel(riskLevel)
                .reason(reason)
                .status("PENDING")
                .build();
    }

    @Builder
    @Data
    public static class SupportTicket {
        @JsonProperty("userId")
        @JsonAlias("usuarioId")
        private Long userId;

        @JsonProperty("riskLevel")
        @JsonAlias("nivelRiesgo")
        private RiskLevel riskLevel;

        @JsonProperty("reason")
        @JsonAlias("razon")
        private String reason;

        @JsonProperty("status")
        @JsonAlias("estado")
        private String status;
    }
}
