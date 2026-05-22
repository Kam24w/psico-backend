package com.psico.app.intervention.controller;

import com.psico.app.common.response.ApiResponse;
import com.psico.app.emotion.model.EmotionType;
import com.psico.app.intervention.model.Recommendation;
import com.psico.app.intervention.service.TherapeuticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/therapy", "/api/terapia"})
@RequiredArgsConstructor
public class TherapeuticController {

    private final TherapeuticService therapeuticService;

    @GetMapping("/exercises")
    public ResponseEntity<ApiResponse<List<Recommendation>>> suggestExercises(@RequestParam EmotionType emotion) {
        return ResponseEntity.ok(ApiResponse.success("Suggested exercises", therapeuticService.suggestExercises(emotion)));
    }
}
