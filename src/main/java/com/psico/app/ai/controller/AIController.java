package com.psico.app.ai.controller;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.psico.app.ai.facade.EmotionPipelineFacade;
import com.psico.app.common.response.ApiResponse;
import com.psico.app.emotion.model.EmotionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final EmotionPipelineFacade pipelineFacade;

    @PostMapping("/message/{usuarioId}")
    public ResponseEntity<ApiResponse<String>> procesarMensaje(
            @PathVariable Long usuarioId,
            @RequestBody AIRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Respuesta generada", pipelineFacade.executePipeline(usuarioId, request.message(), request.emotion()).getContent()));
    }

    public record AIRequest(
            @JsonAlias({"mensaje", "message"}) String message,
            @JsonAlias({"emocion", "emotion"}) EmotionType emotion
    ) {}
}
