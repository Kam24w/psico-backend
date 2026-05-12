package com.psico.app.conversation.controller;

import com.psico.app.common.response.ApiResponse;
import com.psico.app.conversation.model.Conversation;
import com.psico.app.conversation.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
public class SessionController {

    private final ConversationService conversationService;

    @GetMapping("/activa/{usuarioId}")
    public ResponseEntity<ApiResponse<Conversation>> obtenerConversacionActiva(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ApiResponse.success("Conversación activa recuperada", conversationService.getUserConversations(usuarioId).stream().findFirst().orElse(null)));
    }
}
