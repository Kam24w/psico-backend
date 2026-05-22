package com.psico.app.ai.controller;

import com.psico.app.ai.facade.EmotionPipelineFacade;
import com.psico.app.common.response.ApiResponse;
import com.psico.app.emotion.model.TipoEmocion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
public class AIController {

    private final EmotionPipelineFacade pipelineFacade;

    @PostMapping("/mensajes/{usuarioId}")
    public ResponseEntity<ApiResponse<String>> procesarMensaje(
            @PathVariable Long usuarioId,
            @RequestBody AIRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Respuesta generada", pipelineFacade.ejecutarPipeline(usuarioId, request.mensaje(), request.emocion()).getContenido()));
    }

    public record AIRequest(String mensaje, TipoEmocion emocion) {}
}
