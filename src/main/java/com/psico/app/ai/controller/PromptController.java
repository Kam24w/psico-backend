package com.psico.app.ai.controller;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.psico.app.ai.facade.EmotionPipelineFacade;
import com.psico.app.common.response.ApiResponse;
import com.psico.app.emotion.model.EmotionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prompt")
@RequiredArgsConstructor
public class PromptController {

    private final EmotionPipelineFacade pipelineFacade;

    @PostMapping("/{usuarioId}")
    public ResponseEntity<ApiResponse<String>> generarPrompt(
            @PathVariable Long usuarioId,
            @RequestBody PromptRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Prompt generado", pipelineFacade.executePipeline(usuarioId, request.message(), request.emotion()).getContent()));
    }

    public record PromptRequest(
            @JsonAlias({"mensaje", "message"}) String message,
            @JsonAlias({"emocion", "emotion"}) EmotionType emotion
    ) {}
}
