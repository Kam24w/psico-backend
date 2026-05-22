package com.psico.app.support.controller;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.psico.app.common.response.ApiResponse;
import com.psico.app.risk.model.RiskLevel;
import com.psico.app.support.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @PostMapping({"/{userId}/escalate", "/{userId}/escalar"})
    public ResponseEntity<ApiResponse<SupportService.SupportTicket>> escalateCase(
            @PathVariable Long userId,
            @RequestBody SupportRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Caso escalado", supportService.escalateCase(userId, request.reason(), request.riskLevel())));
    }

    public record SupportRequest(
            @JsonAlias({"razon", "reason"}) String reason,
            @JsonAlias({"nivelRiesgo", "riskLevel"}) RiskLevel riskLevel
    ) {}
}
