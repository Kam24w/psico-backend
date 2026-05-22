package com.psico.app.analysis.repository;

import com.psico.app.analysis.model.EmotionalAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmotionalAnalysisRepository extends JpaRepository<EmotionalAnalysis, Long> {
    List<EmotionalAnalysis> findByUserIdOrderByCreatedAtDesc(Long userId);
}
