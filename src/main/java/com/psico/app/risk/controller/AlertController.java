package com.psico.app.risk.controller;

import com.psico.app.common.response.ApiResponse;
import com.psico.app.risk.model.AlertaRiesgo;
import com.psico.app.risk.service.RiskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertController {

    private final RiskService riskService;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<ApiResponse<List<AlertaRiesgo>>> obtenerAlertas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ApiResponse.success("Alertas del usuario", riskService.obtenerAlertas(usuarioId)));
    }
}
