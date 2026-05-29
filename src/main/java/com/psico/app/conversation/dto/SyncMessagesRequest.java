package com.psico.app.conversation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.psico.app.emotion.model.EmotionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncMessagesRequest {
    @NotNull
    @JsonAlias({"usuarioId", "userId"})
    private Long userId;

    @NotBlank
    @Size(max = 2000)
    @JsonAlias({"userContent", "contenidoUsuario"})
    private String userContent;

    @NotBlank
    @Size(max = 4000)
    @JsonAlias({"aiContent", "contenidoAi"})
    private String aiContent;

    @JsonAlias({"emocion", "emotion"})
    private EmotionType emotion;
    
    @JsonAlias({"tipoSesion", "sessionType"})
    private String sessionType = "TEXTO";
}
