package com.psico.app.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    private Long id;
    private Long userId;
    private Integer messageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean active;

    @Deprecated(forRemoval = false)
    public Long getUsuarioId() {
        return userId;
    }

    @Deprecated(forRemoval = false)
    public Integer getMensajes() {
        return messageCount;
    }

    @Deprecated(forRemoval = false)
    public Boolean getActiva() {
        return active;
    }
}
