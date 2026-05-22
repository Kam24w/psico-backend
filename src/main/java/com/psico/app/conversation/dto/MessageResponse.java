package com.psico.app.conversation.dto;

import com.psico.app.emotion.model.EmotionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private String content;
    private String sender;
    private EmotionType associatedEmotion;
    private LocalDateTime createdAt;
    private String rawContent;
}
