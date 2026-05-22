package com.psico.app.conversation.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psico.app.auth.model.User;
import com.psico.app.common.response.ApiResponse;
import com.psico.app.conversation.dto.MensajeResponse;
import com.psico.app.conversation.model.Message;
import com.psico.app.conversation.service.ConversationService;
import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Slf4j
public class InitiateController {

    private final ConversationService conversationService;
    private final UserService userService;

    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<MensajeResponse>> initiateConversation(Principal principal, @RequestBody Map<String, String> body) {
        String email = principal.getName();
        User user = userService.getByEmail(email);
        
        String emotionStr = body.getOrDefault("emotion", "NEUTRAL");
        TipoEmocion emotion;
        try {
            emotion = TipoEmocion.valueOf(emotionStr.toUpperCase());
        } catch (Exception e) {
            emotion = TipoEmocion.NEUTRAL;
        }

        log.info("Initiating voice conversation for user: {} with emotion: {}", email, emotion);

        String tipo = body.getOrDefault("tipo", "VIDEO");
        Message greeting = conversationService.initiateConversation(user.getId(), emotion, tipo);
        
        MensajeResponse response = MensajeResponse.builder()
                .id(greeting.getId())
                .content(greeting.getContenido())
                .rawContent(greeting.getRawContenido())
                .sender("AI")
                .associatedEmotion(greeting.getEmocionAsociada())
                .createdAt(greeting.getFecha())
                .build();

        String debugInfo = greeting.getRawContenido() != null ? " [RAW_OK]" : " [RAW_NULL]";
        return ResponseEntity.ok(ApiResponse.success("Initial greeting generated" + debugInfo, response));
    }
}
