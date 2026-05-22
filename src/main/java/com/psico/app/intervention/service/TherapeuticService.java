package com.psico.app.intervention.service;

import com.psico.app.emotion.model.EmotionType;
import com.psico.app.intervention.model.Recommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TherapeuticService {

    private final RecommendationService recommendationService;

    public List<Recommendation> suggestExercises(EmotionType emotion) {
        return recommendationService.getRecommendations(emotion);
    }
}
