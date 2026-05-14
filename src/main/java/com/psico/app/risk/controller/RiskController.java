package com.psico.app.risk.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.psico.app.common.response.ApiResponse;
import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.risk.model.AlertaRiesgo;
import com.psico.app.risk.model.NivelRiesgo;
import com.psico.app.risk.service.RiskService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/api/risk", "/api/riesgo"})
@RequiredArgsConstructor
public class RiskController {

    private final RiskService riskService;

    @GetMapping({"/{userId}/evaluate", "/{userId}/evalua"})
    public ResponseEntity<ApiResponse<NivelRiesgo>> evaluateRisk(
            @PathVariable Long userId,
            @RequestParam TipoEmocion emotion,
            @RequestParam String message
    ) {
        return ResponseEntity.ok(ApiResponse.success("Risk evaluated", riskService.evaluarRiesgo(userId, emotion, message)));
    }

    @GetMapping({"/{userId}/alerts", "/{userId}/alertas"})
    public ResponseEntity<ApiResponse<List<AlertaRiesgo>>> getAlerts(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Alerts retrieved", riskService.obtenerAlertas(userId)));
    }
}
