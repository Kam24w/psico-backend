package com.psico.app.dashboard.controller;

import com.psico.app.common.response.ApiResponse;
import com.psico.app.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<ApiResponse<DashboardService.DashboardSummary>> obtenerResumen(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ApiResponse.success("Resumen del dashboard", dashboardService.obtenerResumen(usuarioId)));
    }
}
