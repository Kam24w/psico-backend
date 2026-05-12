package com.psico.app.conversation.controller;

import com.psico.app.common.response.ApiResponse;
import com.psico.app.conversation.service.MessageNavigationService;
import com.psico.app.conversation.service.MessageNavigationService.MessageNavigationState;
import com.psico.app.conversation.dto.MensajeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/navegacion")
@RequiredArgsConstructor
public class MessageNavigationController {

    private final MessageNavigationService navigationService;

    @GetMapping("/{usuarioId}/inicial")
    public ResponseEntity<ApiResponse<MessageNavigationState>> inicial(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ApiResponse.success("Estado de navegación", navigationService.construirNavegacion(usuarioId)));
    }

    @GetMapping("/{usuarioId}/anterior")
    public ResponseEntity<ApiResponse<MensajeResponse>> anterior(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ApiResponse.success("Mensaje anterior", navigationService.navegarAnterior(usuarioId)));
    }

    @GetMapping("/{usuarioId}/siguiente")
    public ResponseEntity<ApiResponse<MensajeResponse>> siguiente(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ApiResponse.success("Mensaje siguiente", navigationService.navegarSiguiente(usuarioId)));
    }
}
