package com.psico.app.conversation.service;

import java.util.List;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Message processMessage(@NonNull Long userId, String content, TipoEmocion detectedEmotion) {
        User user = userService.getById(userId);

        Conversation conversation = obtainAndStoreUserMessage(user, content, detectedEmotion);

        TipoEmocion emotion = detectedEmotion != null
                ? detectedEmotion
                : emotionService.getLatestEmotion(userId);

        String aiResponseText = servicioIA.generateResponse(userId, content, toTipoEmocion(emotion));

        return storeAiResponse(conversation, aiResponseText, emotion);
    }

    @Transactional
    protected Conversation obtainAndStoreUserMessage(User user, String content, TipoEmocion detectedEmotion) {
        Conversation conversation = conversationRepository
                .findFirstByUsuarioIdAndActivaTrue(user.getId())
                .orElseGet(() -> createNewConversation(user));

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

    @Transactional
    protected Message storeAiResponse(Conversation conversation, String aiResponseText, TipoEmocion emotion) {
        Message aiResponse = Objects.requireNonNull(Message.builder()
                .contenido(aiResponseText)
                .remitente(Message.Remitente.AI)
                .emocionAsociada(toTipoEmocion(emotion))
                .conversation(conversation)
                .build());
        return Objects.requireNonNull(messageRepository.save(aiResponse));
    }

    public List<Message> getConversationHistory(@NonNull Long conversationId) {
        return messageRepository.findByConversationIdOrderByFechaAsc(conversationId);
    }

    public List<Message> getActiveUserHistory(@NonNull Long userId) {
        return conversationRepository.findFirstByUsuarioIdAndActivaTrue(userId)
                .map(conversation -> messageRepository.findByConversationIdOrderByFechaAsc(conversation.getId()))
                .orElse(List.of());
    }

    public List<Conversation> getUserConversations(@NonNull Long userId) {
        return conversationRepository.findByUsuarioIdOrderByUpdatedAtDesc(userId);
    }

    private TipoEmocion toTipoEmocion(TipoEmocion emotionType) {
        return emotionType;
    }

    private Conversation createNewConversation(User user) {
        Conversation newConversation = Objects.requireNonNull(Conversation.builder()
                .usuario(user)
                .build());
        return Objects.requireNonNull(conversationRepository.save(newConversation));
    }

    @Deprecated(forRemoval = false)
    public Message procesarMensaje(@NonNull Long usuarioId, String contenido, TipoEmocion emocionActual) {
        return processMessage(usuarioId, contenido, emocionActual);
    }

    @Deprecated(forRemoval = false)
    protected Conversation obtenerYGuardarMensajeUsuario(User usuario, String contenido, TipoEmocion emocionActual) {
        return obtainAndStoreUserMessage(usuario, contenido, emocionActual);
    }

    @Deprecated(forRemoval = false)
    protected Message guardarRespuestaIA(Conversation conversacion, String respuestaTexto, TipoEmocion emocion) {
        return storeAiResponse(conversacion, respuestaTexto, emocion);
    }

    @Deprecated(forRemoval = false)
    public List<Message> obtenerHistorial(@NonNull Long conversacionId) {
        return getConversationHistory(conversacionId);
    }

    @Deprecated(forRemoval = false)
    public List<Message> obtenerHistorialActivoUsuario(@NonNull Long usuarioId) {
        return getActiveUserHistory(usuarioId);
    }

    @Deprecated(forRemoval = false)
    public List<Conversation> obtenerConversacionesDeUsuario(@NonNull Long usuarioId) {
        return getUserConversations(usuarioId);
    }
}
