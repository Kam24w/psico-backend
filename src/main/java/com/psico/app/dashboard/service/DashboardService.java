package com.psico.app.dashboard.service;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.psico.app.conversation.model.Conversation;
import com.psico.app.conversation.service.ConversationService;
import com.psico.app.emotion.model.EmotionType;
import com.psico.app.memory.model.UserMemory;
import com.psico.app.memory.service.MemoryService;
import com.psico.app.risk.service.RiskService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ConversationService conversationService;
    private final RiskService riskService;
    private final MemoryService memoryService;

    public DashboardSummary getSummary(Long userId) {
        int activeConversations = !conversationService.getActiveUserHistory(userId).isEmpty() ? 1 : 0;
        
        List<UserMemory> allMemories = memoryService.getMemories(userId);
        int savedMemories = allMemories.size();

        // Calcular finishedChats
        List<Conversation> conversations = conversationService.getUserConversations(userId);
        int finishedChats = (int) conversations.stream().filter(c -> !c.isActive()).count();

        // Calcular newMemoriesThisWeek
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        int newMemoriesThisWeek = (int) allMemories.stream()
                .filter(m -> m.getCreatedAt().isAfter(oneWeekAgo))
                .count();

        // Calcular weeklyProgress
        List<Integer> weeklyProgress = calculateWeeklyProgress(allMemories);

        // Calcular emotionalTrend
        double emotionalTrend = 0.0;
        if (weeklyProgress.size() >= 2) {
            double today = weeklyProgress.get(6);
            double yesterday = weeklyProgress.get(5);
            emotionalTrend = today - yesterday; 
        }

        return DashboardSummary.builder()
                .userId(userId)
                .activeConversations(activeConversations)
                .savedMemories(savedMemories)
                .finishedChats(finishedChats)
                .newMemoriesThisWeek(newMemoriesThisWeek)
                .weeklyProgress(weeklyProgress)
                .emotionalTrend(emotionalTrend)
                .latestAlert(riskService.getAlerts(userId).stream()
                        .max(Comparator.comparing(a -> a.getCreatedAt()))
                        .map(a -> a.getReason())
                        .orElse("Sin alertas recientes"))
                .build();
    }

    private List<Integer> calculateWeeklyProgress(List<UserMemory> memories) {
        List<Integer> progress = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        // Mapa de dia a lista de memorias
        Map<LocalDate, List<UserMemory>> memoriesByDay = memories.stream()
                .collect(Collectors.groupingBy(m -> m.getCreatedAt().toLocalDate()));

        int lastScore = 50; // default a neutral
        
        // Iterar desde hoy - 6 hasta hoy
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            List<UserMemory> dayMemories = memoriesByDay.getOrDefault(date, new ArrayList<>());
            
            if (dayMemories.isEmpty()) {
                progress.add(lastScore);
            } else {
                double avg = dayMemories.stream()
                        .mapToInt(m -> getEmotionScore(m.getAssociatedEmotion()))
                        .average()
                        .orElse(lastScore);
                lastScore = (int) avg;
                progress.add(lastScore);
            }
        }
        
        return progress;
    }

    private int getEmotionScore(EmotionType emotion) {
        if (emotion == null) return 50;
        switch (emotion) {
            case HAPPY: return 90;
            case SURPRISED: return 70;
            case NEUTRAL: return 50;
            case ANXIOUS: return 30;
            case STRESSED: return 30;
            case SAD: return 20;
            case ANGRY: return 20;
            default: return 50;
        }
    }

    @Builder
    @Data
    public static class DashboardSummary {
        @JsonProperty("userId")
        @JsonAlias("usuarioId")
        private Long userId;

        @JsonProperty("activeConversations")
        @JsonAlias("conversacionesActivas")
        private int activeConversations;

        @JsonProperty("savedMemories")
        @JsonAlias("memoriasGuardadas")
        private int savedMemories;

        @JsonProperty("finishedChats")
        private int finishedChats;

        @JsonProperty("newMemoriesThisWeek")
        private int newMemoriesThisWeek;

        @JsonProperty("weeklyProgress")
        private List<Integer> weeklyProgress;

        @JsonProperty("emotionalTrend")
        @JsonAlias("tendenciaEmocional")
        private double emotionalTrend;

        @JsonProperty("latestAlert")
        @JsonAlias("ultimaAlerta")
        private String latestAlert;
    }
}
