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
import com.psico.app.conversation.dto.ConversationResponse;
import com.psico.app.conversation.dto.MessageRequest;
import com.psico.app.conversation.dto.MessageResponse;
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
    @PostMapping("/message")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(@Valid @RequestBody MessageRequest request) {
        String sessionType = request.getSessionType() != null ? request.getSessionType() : "TEXTO";
        Message response = pipelineFacade.executePipeline(
                Objects.requireNonNull(request.getUserId()),
                request.getContent(),
                request.getEmotion(),
                sessionType
        );
        return ResponseEntity.ok(ApiResponse.success("Message processed", convertToResponse(response)));
    }

    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> syncMessages(@Valid @RequestBody SyncMessagesRequest request) {
        List<Message> savedMessages = conversationService.syncMessages(
                Objects.requireNonNull(request.getUserId()),
                request.getUserContent(),
                request.getAiContent(),
                request.getEmotion(),
                request.getSessionType() != null ? request.getSessionType() : "TEXTO"
        );
        return ResponseEntity.ok(ApiResponse.success("Messages synced successfully", savedMessages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList())));
    }

    @GetMapping("/history/{conversationId}")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getHistory(@PathVariable @NonNull Long conversationId) {
        List<Message> history = conversationService.getConversationHistory(conversationId);
        return ResponseEntity.ok(ApiResponse.success("History retrieved", history.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList())));
    }

    @GetMapping("/active-history")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getActiveHistory(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "TEXTO") String sessionType,
            java.security.Principal principal) {
        com.psico.app.auth.model.User user = conversationService.getUserByEmail(principal.getName());
        List<Message> history = conversationService.getActiveUserHistory(user.getId(), sessionType);
        return ResponseEntity.ok(ApiResponse.success("Active session history retrieved", history.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList())));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getUserConversations(@PathVariable @NonNull Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Conversations retrieved", conversationService.getUserConversations(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList())));
    }

    @PostMapping("/active/close")
    public ResponseEntity<ApiResponse<Void>> closeActiveSession(
            @org.springframework.web.bind.annotation.RequestParam Long userId,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "TEXTO") String sessionType) {
        conversationService.closeActiveSession(userId, sessionType);
        return ResponseEntity.ok(ApiResponse.success("Active session closed successfully", null));
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable @NonNull Long conversationId) {
        conversationService.deleteSession(conversationId);
        return ResponseEntity.ok(ApiResponse.success("Session deleted successfully", null));
    }

    private MessageResponse convertToResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .rawContent(message.getRawContent())
                .sender(message.getSender().name())
                .associatedEmotion(message.getAssociatedEmotion())
                .createdAt(message.getCreatedAt())
                .build();
    }

    private ConversationResponse convertToResponse(Conversation conversation) {
        return ConversationResponse.builder()
                .id(conversation.getId())
                .userId(conversation.getUser().getId())
                .messageCount(conversation.getMessages() != null ? conversation.getMessages().size() : 0)
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .active(conversation.getActive())
                .type(conversation.getType())
                .build();
    }
}
