package com.psico.app.conversation.service;

import java.util.List;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psico.app.ai.dto.AiResponse;
import com.psico.app.ai.service.ServicioIA;
import com.psico.app.auth.model.User;
import com.psico.app.conversation.model.Conversation;
import com.psico.app.conversation.model.Message;
import com.psico.app.conversation.repository.ConversationRepository;
import com.psico.app.conversation.repository.MessageRepository;
import com.psico.app.emotion.model.TipoEmocion;
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
    private final ServicioIA servicioIA;
    private final EmotionService emotionService;
    private final UserService userService;

    // ── Guardar mensaje de usuario (public para EmotionPipelineFacade) ──────
    @Transactional
    public Conversation obtainAndStoreUserMessage(User user, String content, TipoEmocion detectedEmotion) {
        return obtainAndStoreUserMessage(user, content, detectedEmotion, "TEXTO");
    }

    @Transactional
    public Conversation obtainAndStoreUserMessage(User user, String content, TipoEmocion detectedEmotion, String tipo) {
        Conversation conversation = conversationRepository
                .findFirstByUsuarioIdAndActivaTrueAndTipo(user.getId(), tipo)
                .orElseGet(() -> createNewConversation(user, tipo));

        Message userMessage = Objects.requireNonNull(Message.builder()
                .contenido(content)
                .remitente(Message.Remitente.USER)
                .emocionAsociada(toTipoEmocion(detectedEmotion))
                .conversation(conversation)
                .build());
        messageRepository.save(userMessage);

        conversation.setUpdatedAt(java.time.LocalDateTime.now());
        return conversationRepository.save(Objects.requireNonNull(conversation));
    }

    // ── Guardar respuesta de la IA (public para EmotionPipelineFacade) ──────
    @Transactional
    public Message storeAiResponse(Conversation conversation, AiResponse aiResponse, TipoEmocion emotion) {
        Message message = Objects.requireNonNull(Message.builder()
                .contenido(aiResponse.getCleaned())
                .rawContenido(aiResponse.getRaw())
                .remitente(Message.Remitente.AI)
                .emocionAsociada(toTipoEmocion(emotion))
                .conversation(conversation)
                .build());
        return Objects.requireNonNull(messageRepository.save(message));
    }

    // ── Sincronizar mensajes (VIDEO/TEXTO) ───────────────────────────────────
    @Transactional
    public List<Message> syncMessages(@NonNull Long userId, String userContent, String aiContent, TipoEmocion emotion, String tipo) {
        User user = userService.getById(userId);
        Conversation conversation = conversationRepository
                .findFirstByUsuarioIdAndActivaTrueAndTipo(user.getId(), tipo)
                .orElseGet(() -> createNewConversation(user, tipo));

        Message userMessage = Objects.requireNonNull(Message.builder()
                .contenido(userContent)
                .remitente(Message.Remitente.USER)
                .emocionAsociada(toTipoEmocion(emotion))
                .conversation(conversation)
                .build());
        messageRepository.save(userMessage);

        Message aiMessage = Objects.requireNonNull(Message.builder()
                .contenido(aiContent)
                .rawContenido(aiContent)
                .remitente(Message.Remitente.AI)
                .emocionAsociada(toTipoEmocion(emotion))
                .conversation(conversation)
                .build());
        messageRepository.save(aiMessage);

        conversation.setUpdatedAt(java.time.LocalDateTime.now());
        conversationRepository.save(conversation);

        return List.of(userMessage, aiMessage);
    }

    // ── Historial ────────────────────────────────────────────────────────────
    public List<Message> getConversationHistory(@NonNull Long conversationId) {
        return messageRepository.findByConversationIdOrderByFechaAsc(conversationId);
    }

    /** Compatibilidad con EmotionPipelineFacade de main (sin tipoSesion) */
    public List<Message> getActiveUserHistory(@NonNull Long userId) {
        return getActiveUserHistory(userId, "TEXTO");
    }

    public List<Message> getActiveUserHistory(@NonNull Long userId, String tipo) {
        return conversationRepository.findFirstByUsuarioIdAndActivaTrueAndTipo(userId, tipo)
                .map(conversation -> messageRepository.findByConversationIdOrderByFechaAsc(conversation.getId()))
                .orElse(List.of());
    }

    public List<Conversation> getUserConversations(@NonNull Long userId) {
        return conversationRepository.findByUsuarioIdOrderByUpdatedAtDesc(userId);
    }

    // ── Iniciar conversación (saludo inicial IA) ─────────────────────────────
    @Transactional
    public Message initiateConversation(@NonNull Long userId, TipoEmocion emotion) {
        return initiateConversation(userId, emotion, "TEXTO");
    }

    @Transactional
    public Message initiateConversation(@NonNull Long userId, TipoEmocion emotion, String tipo) {
        User user = userService.getById(userId);
        Conversation conversation = conversationRepository
                .findFirstByUsuarioIdAndActivaTrueAndTipo(user.getId(), tipo)
                .orElseGet(() -> createNewConversation(user, tipo));

        AiResponse aiGreeting = servicioIA.generateInitialGreeting(userId, user.getNombre(), emotion);

        return storeAiResponse(conversation, aiGreeting, emotion);
    }

    // ── Cerrar / Eliminar sesión ─────────────────────────────────────────────
    @Transactional
    public void closeActiveSession(Long userId, String tipo) {
        conversationRepository.findFirstByUsuarioIdAndActivaTrueAndTipo(userId, tipo).ifPresent(conv -> {
            conv.setActiva(false);
            conversationRepository.save(conv);
        });
    }

    @Transactional
    public void deleteSession(Long conversationId) {
        conversationRepository.deleteById(conversationId);
    }

    // ── Utilidades ───────────────────────────────────────────────────────────
    public User getUserByEmail(String email) {
        return userService.getByEmail(email);
    }

    private TipoEmocion toTipoEmocion(TipoEmocion emotionType) {
        return emotionType;
    }

    private Conversation createNewConversation(User user, String tipo) {
        Conversation newConversation = Objects.requireNonNull(Conversation.builder()
                .usuario(user)
                .tipo(tipo)
                .build());
        return Objects.requireNonNull(conversationRepository.save(newConversation));
    }

    // ── Aliases deprecated (compatibilidad hacia atrás) ──────────────────────
    @Deprecated(forRemoval = false)
    public List<Message> obtenerHistorial(@NonNull Long conversacionId) {
        return getConversationHistory(conversacionId);
    }

    @Deprecated(forRemoval = false)
    public List<Message> obtenerHistorialActivoUsuario(@NonNull Long usuarioId) {
        return getActiveUserHistory(usuarioId, "TEXTO");
    }

    @Deprecated(forRemoval = false)
    public List<Conversation> obtenerConversacionesDeUsuario(@NonNull Long usuarioId) {
        return getUserConversations(usuarioId);
    }
}
