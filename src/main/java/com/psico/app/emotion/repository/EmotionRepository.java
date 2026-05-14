package com.psico.app.emotion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.psico.app.emotion.model.Emotion;

@Repository
public interface EmotionRepository extends JpaRepository<Emotion, Long> {

    List<Emotion> findByUserIdOrderByDetectedAtDesc(Long userId);

    @Query("SELECT e FROM Emotion e WHERE e.user.id = :userId ORDER BY e.detectedAt DESC")
    Emotion findLatestEmotionByUserId(Long userId);
}
