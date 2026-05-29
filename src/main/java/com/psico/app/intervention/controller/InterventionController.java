package com.psico.app.intervention.controller;

import com.psico.app.common.response.ApiResponse;
import com.psico.app.emotion.model.EmotionType;
import com.psico.app.intervention.model.Recommendation;
import com.psico.app.intervention.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/intervention")
@RequiredArgsConstructor
public class InterventionController {

    private final RecommendationService recommendationService;

    @GetMapping("/{emotion}")
    public ResponseEntity<ApiResponse<List<Recommendation>>> getRecommendations(@PathVariable EmotionType emotion) {
        return ResponseEntity.ok(ApiResponse.success("Recommendations loaded", recommendationService.getRecommendations(emotion)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Recommendation>> createRecommendation(@RequestBody Recommendation recommendation) {
        return ResponseEntity.ok(ApiResponse.success("Recommendation saved", recommendationService.saveRecommendation(recommendation)));
    }
}
