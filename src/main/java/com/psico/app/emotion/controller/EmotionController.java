package com.psico.app.emotion.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.psico.app.common.response.ApiResponse;
import com.psico.app.emotion.model.Emotion;
import com.psico.app.emotion.model.EmotionType;
import com.psico.app.emotion.service.EmotionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;

    @PostMapping
    public ResponseEntity<ApiResponse<Emotion>> registerEmotion(@Valid @RequestBody EmotionRequest request) {
        Emotion emotion = emotionService.registerEmotion(
                Objects.requireNonNull(request.getUserId()),
                request.getEmotionType(),
                request.getIntensity()
        );
        return ResponseEntity.ok(ApiResponse.success("Emotion registered", emotion));
    }

    @GetMapping("/latest/{userId}")
    public ResponseEntity<ApiResponse<EmotionType>> getLatestEmotion(@PathVariable @NonNull Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Latest emotion retrieved", emotionService.getLatestEmotion(userId)));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<ApiResponse<List<Emotion>>> getEmotionHistory(@PathVariable @NonNull Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Emotion history retrieved", emotionService.getEmotionHistory(userId)));
    }

    @Data
    public static class EmotionRequest {
        @NotNull
        @JsonAlias("usuarioId")
        private Long userId;

        @NotNull
        @JsonAlias("tipo")
        private EmotionType emotionType;

        @NotNull
        @JsonAlias("intensidad")
        private Double intensity;
    }
}
