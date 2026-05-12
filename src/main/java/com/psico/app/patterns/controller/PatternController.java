package com.psico.app.patterns.controller;

import com.psico.app.common.response.ApiResponse;
import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.patterns.service.PatternService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patron")
@RequiredArgsConstructor
public class PatternController {

    private final PatternService patternService;

    @GetMapping("/analizar")
    public ResponseEntity<ApiResponse<PatternService.PatternResult>> analizarMensaje(
            @RequestParam TipoEmocion emocion,
            @RequestParam String mensaje
    ) {
        return ResponseEntity.ok(ApiResponse.success("Patrón detectado", patternService.analizarTexto(mensaje, emocion)));
    }
}
