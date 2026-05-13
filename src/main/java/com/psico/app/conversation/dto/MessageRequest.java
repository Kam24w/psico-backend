package com.psico.app.conversation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.psico.app.emotion.model.TipoEmocion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
    @NotNull
    @JsonAlias("usuarioId")
    private Long userId;

    @NotBlank
    @Size(max = 2000)
    @JsonAlias("contenido")
    private String content;

    @JsonAlias("emocion")
    private TipoEmocion emotion;

    public Long getUsuarioId() {
        return userId;
    }

    public String getContenido() {
        return content;
    }

    public TipoEmocion getEmocion() {
        return emotion;
    }
}
