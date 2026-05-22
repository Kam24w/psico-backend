package com.psico.app.intervention.service;

import com.psico.app.emotion.model.EmotionType;
import com.psico.app.intervention.model.Recommendation;
import com.psico.app.intervention.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public List<Recommendation> getRecommendations(EmotionType emotion) {
        return recommendationRepository.findByInitialStateOrderByPriorityAsc(emotion);
    }

    @Transactional
    public Recommendation saveRecommendation(Recommendation recommendation) {
        return recommendationRepository.save(recommendation);
    }
}
