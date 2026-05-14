package com.psico.app.conversation.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psico.app.common.response.ApiResponse;
import com.psico.app.conversation.dto.ConversacionResponse;
import com.psico.app.conversation.dto.MensajeRequest;
import com.psico.app.conversation.dto.MensajeResponse;
import com.psico.app.conversation.model.Conversation;
import com.psico.app.conversation.model.Message;
import com.psico.app.conversation.service.ConversationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping({"/message", "/mensaje"})
    public ResponseEntity<ApiResponse<MensajeResponse>> sendMessage(@Valid @RequestBody MensajeRequest request) {
        Message response = conversationService.processMessage(
                Objects.requireNonNull(request.getUsuarioId()),
                request.getContenido(),
                request.getEmocion()
        );
        return ResponseEntity.ok(ApiResponse.success("Message processed", convertToResponse(response)));
    }

    @GetMapping({"/history/{conversationId}", "/historial/{conversationId}"})
    public ResponseEntity<ApiResponse<List<MensajeResponse>>> getHistory(@PathVariable @NonNull Long conversationId) {
        List<Message> history = conversationService.getConversationHistory(conversationId);
        return ResponseEntity.ok(ApiResponse.success("History retrieved", history.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList())));
    }

    @GetMapping("/active-history")
    public ResponseEntity<ApiResponse<List<MensajeResponse>>> getActiveHistory(java.security.Principal principal) {
        // Obtenemos el usuario por email (JWT)
        com.psico.app.auth.model.User user = conversationService.getUserByEmail(principal.getName());
        List<Message> history = conversationService.getActiveUserHistory(user.getId());
        
        return ResponseEntity.ok(ApiResponse.success("Active session history retrieved", history.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList())));
    }

    @GetMapping({"/user/{userId}", "/usuario/{userId}"})
    public ResponseEntity<ApiResponse<List<ConversacionResponse>>> getUserConversations(@PathVariable @NonNull Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Conversations retrieved", conversationService.getUserConversations(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList())));
    }

    private MensajeResponse convertToResponse(Message mensaje) {
        return MensajeResponse.builder()
                .id(mensaje.getId())
                .content(mensaje.getContenido())
                .sender(mensaje.getRemitente().name())
                .associatedEmotion(mensaje.getEmocionAsociada())
                .createdAt(mensaje.getFecha())
                .build();
    }

    private ConversacionResponse convertToResponse(Conversation conversacion) {
        return ConversacionResponse.builder()
                .id(conversacion.getId())
                .userId(conversacion.getUsuario().getId())
                .messageCount(conversacion.getMensajes() != null ? conversacion.getMensajes().size() : 0)
                .createdAt(conversacion.getCreatedAt())
                .updatedAt(conversacion.getUpdatedAt())
                .active(conversacion.getActiva())
                .build();
    }
}
