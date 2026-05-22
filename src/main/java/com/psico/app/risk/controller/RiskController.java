package com.psico.app.risk.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.psico.app.common.response.ApiResponse;
import com.psico.app.emotion.model.EmotionType;
import com.psico.app.risk.model.RiskAlert;
import com.psico.app.risk.model.RiskLevel;
import com.psico.app.risk.service.RiskService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
public class RiskController {

    private final RiskService riskService;

    @GetMapping("/{userId}/evaluate")
    public ResponseEntity<ApiResponse<RiskLevel>> evaluateRisk(
            @PathVariable Long userId,
            @RequestParam EmotionType emotion,
            @RequestParam String message
    ) {
        return ResponseEntity.ok(ApiResponse.success("Risk evaluated", riskService.evaluateRisk(userId, emotion, message)));
    }

    @GetMapping("/{userId}/alerts")
    public ResponseEntity<ApiResponse<List<RiskAlert>>> getAlerts(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Alerts retrieved", riskService.getAlerts(userId)));
    }
}
