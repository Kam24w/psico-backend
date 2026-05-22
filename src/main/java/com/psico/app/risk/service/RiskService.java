package com.psico.app.risk.service;

import com.psico.app.emotion.model.EmotionType;
import com.psico.app.risk.model.RiskAlert;
import com.psico.app.risk.model.RiskLevel;
import com.psico.app.risk.repository.RiskAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskService {

    private final RiskAlertRepository alertRepository;

    public RiskLevel evaluateRisk(Long userId, EmotionType emotion, String message) {
        if (emotion == EmotionType.ANGRY || emotion == EmotionType.STRESSED) {
            return RiskLevel.HIGH;
        }
        if (emotion == EmotionType.ANXIOUS || message.contains("no puedo")) {
            return RiskLevel.MEDIUM;
        }
        return RiskLevel.LOW;
    }

    @Transactional
    public RiskAlert registerAlert(Long userId, RiskLevel level, String reason) {
        RiskAlert alert = RiskAlert.builder()
                .userId(userId)
                .level(level)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();
        return alertRepository.save(alert);
    }

    public List<RiskAlert> getAlerts(Long userId) {
        return alertRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
