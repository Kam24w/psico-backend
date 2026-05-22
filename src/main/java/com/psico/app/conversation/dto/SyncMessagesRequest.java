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
public class SyncMessagesRequest {
    @NotNull
    @JsonAlias("usuarioId")
    private Long userId;

    @NotBlank
    @Size(max = 2000)
    @JsonAlias("userContent")
    private String userContent;

    @NotBlank
    @Size(max = 4000)
    @JsonAlias("aiContent")
    private String aiContent;

    @JsonAlias("emocion")
    private TipoEmocion emotion;
    
    @JsonAlias("tipoSesion")
    private String tipoSesion = "TEXTO";

    public Long getUsuarioId() {
        return userId;
    }
}
