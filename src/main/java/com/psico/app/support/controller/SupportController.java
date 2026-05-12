package com.psico.app.support.controller;

import com.psico.app.common.response.ApiResponse;
import com.psico.app.risk.model.NivelRiesgo;
import com.psico.app.support.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @PostMapping("/{usuarioId}/escalar")
    public ResponseEntity<ApiResponse<SupportService.SupportTicket>> escalarCaso(
            @PathVariable Long usuarioId,
            @RequestBody SupportRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Caso escalado", supportService.escalarCaso(usuarioId, request.razon(), request.nivelRiesgo())));
    }

    public record SupportRequest(String razon, NivelRiesgo nivelRiesgo) {}
}
