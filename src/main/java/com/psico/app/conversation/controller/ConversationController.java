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

import com.psico.app.ai.facade.EmotionPipelineFacade;
import com.psico.app.common.response.ApiResponse;
import com.psico.app.conversation.dto.ConversacionResponse;
import com.psico.app.conversation.dto.MensajeRequest;
import com.psico.app.conversation.dto.MensajeResponse;
import com.psico.app.conversation.dto.SyncMessagesRequest;
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
    private final EmotionPipelineFacade pipelineFacade;

    /**
     * Envía un mensaje de texto. Usa el pipeline de IA (EmotionPipelineFacade)
     * y aísla la conversación según tipoSesion (TEXTO o VIDEO).
     */
    @PostMapping({"/message", "/mensaje"})
    public ResponseEntity<ApiResponse<MensajeResponse>> sendMessage(@Valid @RequestBody MensajeRequest request) {
        String tipoSesion = request.getTipoSesion() != null ? request.getTipoSesion() : "TEXTO";
        Message response = pipelineFacade.ejecutarPipeline(
                Objects.requireNonNull(request.getUsuarioId()),
                request.getContenido(),
                request.getEmocion(),
                tipoSesion
        );
        return ResponseEntity.ok(ApiResponse.success("Message processed", convertToResponse(response)));
    }

    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<List<MensajeResponse>>> syncMessages(@Valid @RequestBody SyncMessagesRequest request) {
        List<Message> savedMessages = conversationService.syncMessages(
                Objects.requireNonNull(request.getUsuarioId()),
                request.getUserContent(),
                request.getAiContent(),
                request.getEmotion(),
                request.getTipoSesion() != null ? request.getTipoSesion() : "TEXTO"
        );
        return ResponseEntity.ok(ApiResponse.success("Messages synced successfully", savedMessages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList())));
    }

    @GetMapping({"/history/{conversationId}", "/historial/{conversationId}"})
    public ResponseEntity<ApiResponse<List<MensajeResponse>>> getHistory(@PathVariable @NonNull Long conversationId) {
        List<Message> history = conversationService.getConversationHistory(conversationId);
        return ResponseEntity.ok(ApiResponse.success("History retrieved", history.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList())));
    }

    @GetMapping("/active-history")
    public ResponseEntity<ApiResponse<List<MensajeResponse>>> getActiveHistory(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "TEXTO") String tipoSesion,
            java.security.Principal principal) {
        com.psico.app.auth.model.User user = conversationService.getUserByEmail(principal.getName());
        List<Message> history = conversationService.getActiveUserHistory(user.getId(), tipoSesion);
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

    @PostMapping("/active/close")
    public ResponseEntity<ApiResponse<Void>> closeActiveSession(
            @org.springframework.web.bind.annotation.RequestParam Long userId,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "TEXTO") String tipoSesion) {
        conversationService.closeActiveSession(userId, tipoSesion);
        return ResponseEntity.ok(ApiResponse.success("Active session closed successfully", null));
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable @NonNull Long conversationId) {
        conversationService.deleteSession(conversationId);
        return ResponseEntity.ok(ApiResponse.success("Session deleted successfully", null));
    }

    private MensajeResponse convertToResponse(Message mensaje) {
        return MensajeResponse.builder()
                .id(mensaje.getId())
                .content(mensaje.getContenido())
                .rawContent(mensaje.getRawContenido())
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
                .tipo(conversacion.getTipo())
                .build();
    }
}
