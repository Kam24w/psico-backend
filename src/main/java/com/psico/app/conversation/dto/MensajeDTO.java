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
public class MensajeDTO {
    private Long id;
    private String contenido;
    private String remitente; // "USER" o "AI"
    private TipoEmocion emocionAsociada;
    private LocalDateTime fecha;
}
