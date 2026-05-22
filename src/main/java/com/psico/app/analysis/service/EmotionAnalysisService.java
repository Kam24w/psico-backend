package com.psico.app.analysis.service;

import com.psico.app.analysis.model.EmotionalAnalysis;
import com.psico.app.analysis.repository.EmotionalAnalysisRepository;
import com.psico.app.conversation.model.Message;
import com.psico.app.emotion.model.EmotionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmotionAnalysisService {

    private final EmotionalAnalysisRepository analysisRepository;

    @Transactional
    public EmotionalAnalysis analyzeConversation(Long userId, List<Message> messages) {
        long positiveCount = messages.stream()
                .filter(m -> m.getAssociatedEmotion() == EmotionType.HAPPY || m.getAssociatedEmotion() == EmotionType.SURPRISED)
                .count();

        long negativeCount = messages.stream()
                .filter(m -> m.getAssociatedEmotion() == EmotionType.SAD || m.getAssociatedEmotion() == EmotionType.STRESSED || m.getAssociatedEmotion() == EmotionType.ANXIOUS || m.getAssociatedEmotion() == EmotionType.ANGRY)
                .count();

        EmotionalAnalysis analysis = EmotionalAnalysis.builder()
                .userId(userId)
                .analyzedMessages(messages.size())
                .positive((int) positiveCount)
                .negative((int) negativeCount)
                .createdAt(LocalDateTime.now())
                .build();

        return analysisRepository.save(analysis);
    }

    public double getTrend(List<EmotionalAnalysis> history) {
        if (history.isEmpty()) {
            return 0.0;
        }
        return history.stream()
                .mapToDouble(a -> a.getPositive() - a.getNegative())
                .average()
                .orElse(0.0);
    }
}
