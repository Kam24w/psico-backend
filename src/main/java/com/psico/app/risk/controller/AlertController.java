package com.psico.app.risk.controller;

import com.psico.app.common.response.ApiResponse;
import com.psico.app.risk.model.RiskAlert;
import com.psico.app.risk.service.RiskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/alerts", "/api/alertas"})
@RequiredArgsConstructor
public class AlertController {

    private final RiskService riskService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<RiskAlert>>> getAlerts(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Alertas del usuario", riskService.getAlerts(userId)));
    }
}
