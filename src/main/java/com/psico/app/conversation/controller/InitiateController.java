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
import com.psico.app.conversation.dto.MessageResponse;
import com.psico.app.conversation.model.Message;
import com.psico.app.conversation.service.ConversationService;
import com.psico.app.emotion.model.EmotionType;
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
    public ResponseEntity<ApiResponse<MessageResponse>> initiateConversation(Principal principal, @RequestBody Map<String, String> body) {
        String email = principal.getName();
        User user = userService.getByEmail(email);
        
        String emotionStr = body.getOrDefault("emotion", "NEUTRAL");
        EmotionType emotion;
        try {
            emotion = EmotionType.valueOf(emotionStr.toUpperCase());
        } catch (Exception e) {
            emotion = EmotionType.NEUTRAL;
        }

        log.info("Initiating voice conversation for user: {} with emotion: {}", email, emotion);

        String type = body.getOrDefault("tipo", "VIDEO");
        Message greeting = conversationService.initiateConversation(user.getId(), emotion, type);
        
        MessageResponse response = MessageResponse.builder()
                .id(greeting.getId())
                .content(greeting.getContent())
                .rawContent(greeting.getRawContent())
                .sender("AI")
                .associatedEmotion(greeting.getAssociatedEmotion())
                .createdAt(greeting.getCreatedAt())
                .build();

        String debugInfo = greeting.getRawContent() != null ? " [RAW_OK]" : " [RAW_NULL]";
        return ResponseEntity.ok(ApiResponse.success("Initial greeting generated" + debugInfo, response));
    }
}
