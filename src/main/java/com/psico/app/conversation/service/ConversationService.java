package com.psico.app.conversation.service;

import java.util.List;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psico.app.ai.dto.AiResponse;
import com.psico.app.ai.service.AIService;
import com.psico.app.auth.model.User;
import com.psico.app.conversation.model.Conversation;
import com.psico.app.conversation.model.Message;
import com.psico.app.conversation.repository.ConversationRepository;
import com.psico.app.conversation.repository.MessageRepository;
import com.psico.app.emotion.model.EmotionType;
import com.psico.app.emotion.service.EmotionService;
import com.psico.app.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AIService aiService;
    private final EmotionService emotionService;
    private final UserService userService;

    // ── Save user message ──────
    @Transactional
    public Conversation obtainAndStoreUserMessage(User user, String content, EmotionType detectedEmotion) {
        return obtainAndStoreUserMessage(user, content, detectedEmotion, "TEXTO");
    }

    @Transactional
    public Conversation obtainAndStoreUserMessage(User user, String content, EmotionType detectedEmotion, String type) {
        Conversation conversation = conversationRepository
                .findFirstByUserIdAndActiveTrueAndType(user.getId(), type)
                .orElseGet(() -> createNewConversation(user, type));

        Message userMessage = Objects.requireNonNull(Message.builder()
                .content(content)
                .sender(Message.Sender.USER)
                .associatedEmotion(detectedEmotion)
                .conversation(conversation)
                .build());
        messageRepository.save(userMessage);

        conversation.setUpdatedAt(java.time.LocalDateTime.now());
        return conversationRepository.save(Objects.requireNonNull(conversation));
    }

    // ── Save AI response ──────
    @Transactional
    public Message storeAiResponse(Conversation conversation, AiResponse aiResponse, EmotionType emotion) {
        Message message = Objects.requireNonNull(Message.builder()
                .content(aiResponse.getCleaned())
                .rawContent(aiResponse.getRaw())
                .sender(Message.Sender.AI)
                .associatedEmotion(emotion)
                .conversation(conversation)
                .build());
        return Objects.requireNonNull(messageRepository.save(message));
    }

    // ── Sync messages (VIDEO/TEXTO) ───────────────────────────────────
    @Transactional
    public List<Message> syncMessages(@NonNull Long userId, String userContent, String aiContent, EmotionType emotion, String type) {
        User user = userService.getById(userId);
        Conversation conversation = conversationRepository
                .findFirstByUserIdAndActiveTrueAndType(user.getId(), type)
                .orElseGet(() -> createNewConversation(user, type));

        Message userMessage = Objects.requireNonNull(Message.builder()
                .content(userContent)
                .sender(Message.Sender.USER)
                .associatedEmotion(emotion)
                .conversation(conversation)
                .build());
        messageRepository.save(userMessage);

        Message aiMessage = Objects.requireNonNull(Message.builder()
                .content(aiContent)
                .rawContent(aiContent)
                .sender(Message.Sender.AI)
                .associatedEmotion(emotion)
                .conversation(conversation)
                .build());
        messageRepository.save(aiMessage);

        conversation.setUpdatedAt(java.time.LocalDateTime.now());
        conversationRepository.save(conversation);

        return List.of(userMessage, aiMessage);
    }

    // ── History ────────────────────────────────────────────────────────────
    public List<Message> getConversationHistory(@NonNull Long conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    public List<Message> getActiveUserHistory(@NonNull Long userId) {
        return getActiveUserHistory(userId, "TEXTO");
    }

    public List<Message> getActiveUserHistory(@NonNull Long userId, String type) {
        return conversationRepository.findFirstByUserIdAndActiveTrueAndType(userId, type)
                .map(conversation -> messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId()))
                .orElse(List.of());
    }

    public List<Conversation> getUserConversations(@NonNull Long userId) {
        return conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    // ── Initiate conversation (initial AI greeting) ─────────────────────────────
    @Transactional
    public Message initiateConversation(@NonNull Long userId, EmotionType emotion) {
        return initiateConversation(userId, emotion, "TEXTO");
    }

    @Transactional
    public Message initiateConversation(@NonNull Long userId, EmotionType emotion, String type) {
        User user = userService.getById(userId);
        Conversation conversation = conversationRepository
                .findFirstByUserIdAndActiveTrueAndType(user.getId(), type)
                .orElseGet(() -> createNewConversation(user, type));

        AiResponse aiGreeting = aiService.generateInitialGreeting(userId, user.getName(), emotion);

        return storeAiResponse(conversation, aiGreeting, emotion);
    }

    // ── Close / Delete session ─────────────────────────────────────────────
    @Transactional
    public void closeActiveSession(Long userId, String type) {
        conversationRepository.findFirstByUserIdAndActiveTrueAndType(userId, type).ifPresent(conv -> {
            conv.setActive(false);
            conversationRepository.save(conv);
        });
    }

    @Transactional
    public void deleteSession(Long conversationId) {
        conversationRepository.deleteById(conversationId);
    }

    // ── Utilities ───────────────────────────────────────────────────────────
    public User getUserByEmail(String email) {
        return userService.getByEmail(email);
    }

    private Conversation createNewConversation(User user, String type) {
        Conversation newConversation = Objects.requireNonNull(Conversation.builder()
                .user(user)
                .type(type)
                .build());
        return Objects.requireNonNull(conversationRepository.save(newConversation));
    }
}
