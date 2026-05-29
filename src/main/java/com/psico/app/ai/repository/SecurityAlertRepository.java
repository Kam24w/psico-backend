package com.psico.app.ai.repository;

import com.psico.app.ai.model.SecurityAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SecurityAlertRepository extends JpaRepository<SecurityAlert, Long> {
    List<SecurityAlert> findByReviewedFalse();
    List<SecurityAlert> findByUserIdOrderByDetectedAtDesc(Long userId);
}
