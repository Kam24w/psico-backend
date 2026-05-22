package com.psico.app.risk.repository;

import com.psico.app.risk.model.RiskAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskAlertRepository extends JpaRepository<RiskAlert, Long> {
    List<RiskAlert> findByUserIdOrderByCreatedAtDesc(Long userId);
}
