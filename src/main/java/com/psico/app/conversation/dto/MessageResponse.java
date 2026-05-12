package com.psico.app.conversation.dto;

import com.psico.app.emotion.model.TipoEmocion;
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
    private TipoEmocion associatedEmotion;
    private LocalDateTime createdAt;

    @Deprecated(forRemoval = false)
    public String getContenido() {
        return content;
    }

    @Deprecated(forRemoval = false)
    public String getRemitente() {
        return sender;
    }

    @Deprecated(forRemoval = false)
    public TipoEmocion getEmocionAsociada() {
        return associatedEmotion;
    }

    @Deprecated(forRemoval = false)
    public LocalDateTime getFecha() {
        return createdAt;
    }
}
