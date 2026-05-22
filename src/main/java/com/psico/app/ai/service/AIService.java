package com.psico.app.ai.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psico.app.ai.client.AIClient;
import com.psico.app.ai.dto.AiResponse;
import com.psico.app.ai.model.SecurityAlert;
import com.psico.app.ai.repository.SecurityAlertRepository;
import com.psico.app.ai.repository.ContextMemoryRepository;
import com.psico.app.ai.repository.AIPersonalityRepository;
import com.psico.app.auth.model.User;
import com.psico.app.emotion.model.EmotionType;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {

    private final com.psico.app.ai.client.AIProviderFactory aiProviderFactory;
    private final ResponseGenerator responseGenerator;
    private final SecurityAlertRepository alertRepository;
    private final AIPersonalityRepository personalityRepository;
    private final ContextMemoryRepository memoryRepository;
    private final EntityManager entityManager;

    public AiResponse generateResponse(Long userId, String userMessage, EmotionType emotion) {
        log.info("--- START AI GENERATION ---");
        log.info("User ID: {}, Message: {}, Emotion: {}", userId, userMessage, emotion);

        // Detect risk first — the level determines which model will be used
        int riskLevel = detectRisks(userId, userMessage);
        if (riskLevel > 0) {
            log.warn("Risk level {} detected — escalating to 70B model", riskLevel);
        }

        String personalityPrompt = personalityRepository.findByActiveTrue()
                .map(p -> p.getSystemPrompt())
                .orElse("Eres un asistente emocional empático.");

        String memoryContext = getRecentMemory(userId);

        String systemPrompt   = responseGenerator.buildSystemPrompt(emotion, personalityPrompt);
        String finalUserMessage = responseGenerator.buildUserMessage(userMessage, emotion, memoryContext);

        log.info("SYSTEM PROMPT:\n{}", systemPrompt);
        log.info("USER MESSAGE:\n{}", finalUserMessage);

        // Pass risk level: 0 → fast 8B, >0 → empathetic 70B
        String rawResponse = aiProviderFactory.getProvider().sendMessageWithRisk(systemPrompt, finalUserMessage, riskLevel);

        log.info("RAW RESPONSE FROM AI:\n\"{}\"", rawResponse);

        String cleaned = cleanResponse(rawResponse);

        log.info("FINAL CLEANED RESPONSE:\n\"{}\"", cleaned);
        log.info("--- END AI GENERATION ---");

        return AiResponse.builder()
                .raw(rawResponse)
                .cleaned(cleaned)
                .build();
    }

    private String cleanResponse(String text) {
        if (text == null || text.isBlank()) return "Hola, estoy aquí para escucharte.";

        log.info("=== AI RAW (passthrough) ===\n\"{}\"", text);

        String result = text.trim()
            .replaceAll("^\"|\"$", "")
            .replaceAll("\\*\\*", "")
            .trim();

        if (result.length() < 3) {
            return "Cuéntame más, estoy escuchando.";
        }

        return result;
    }

    /**
     * Detects risk keywords and saves the alert.
     * Returns the riskLevel so generateResponse can choose the appropriate AI model.
     */
    @Transactional
    public int detectRisks(Long userId, String message) {
        if (message == null) return 0;
        String lower = message.toLowerCase();

        int riskLevel = 0;
        String type = null;

        if (lower.contains("suicidio") || lower.contains("quitarme la vida")
                || lower.contains("matarme") || lower.contains("no quiero vivir")
                || lower.contains("quiero morir") || lower.contains("acabar con mi vida")) {
            riskLevel = 10;
            type = "SUICIDIO";
        } else if (lower.contains("autolesion") || lower.contains("cortarme")
                || lower.contains("hacerme daño") || lower.contains("lastimarme")) {
            riskLevel = 7;
            type = "AUTOLESION";
        } else if (lower.contains("morir") || lower.contains("ya no puedo más")
                || lower.contains("no vale la pena vivir")) {
            riskLevel = 5;
            type = "DESESPERANZA";
        }

        if (type != null) {
            User userRef = entityManager.getReference(User.class, userId);
            String snippet = message.length() > 500 ? message.substring(0, 500) : message;

            SecurityAlert alert = SecurityAlert.builder()
                    .user(userRef)
                    .type(type)
                    .riskLevel(riskLevel)
                    .detectedSnippet(snippet)
                    .build();
            alertRepository.save(alert);
            log.warn("Security alert [{}] level {} detected for user {}", type, riskLevel, userId);
        }

        return riskLevel;
    }

    public AiResponse generateInitialGreeting(Long userId, String userName, EmotionType emotion) {
        log.info("Generating initial greeting for user {} ({}) with emotion {}", userId, userName, emotion);

        String memoryContext = getRecentMemory(userId);
        String systemPrompt = "Eres un asistente psicológico empático iniciando una sesión de voz.";
        String userMessage  = responseGenerator.buildInitialGreetingPrompt(userName, emotion, memoryContext);

        // Initial greeting always uses fast model (riskLevel = 0)
        String rawResponse = aiProviderFactory.getProvider().sendMessage(systemPrompt, userMessage);
        log.info("RAW GREETING FROM AI:\n\"{}\"", rawResponse);

        return AiResponse.builder()
                .raw(rawResponse)
                .cleaned(cleanResponse(rawResponse))
                .build();
    }

    private String getRecentMemory(Long userId) {
        return memoryRepository.findByUserId(userId).stream()
                .limit(5)
                .map(m -> m.getKey() + ": " + m.getValue())
                .reduce("", (a, b) -> a + "\n" + b);
    }
}
