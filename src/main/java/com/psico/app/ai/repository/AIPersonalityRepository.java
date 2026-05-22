package com.psico.app.ai.repository;

import com.psico.app.ai.model.AIPersonality;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AIPersonalityRepository extends JpaRepository<AIPersonality, Long> {
    Optional<AIPersonality> findByActiveTrue();
}
