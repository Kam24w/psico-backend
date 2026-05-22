package com.psico.app.intervention.repository;

import com.psico.app.emotion.model.EmotionType;
import com.psico.app.intervention.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByInitialStateOrderByPriorityAsc(EmotionType initialState);
}
