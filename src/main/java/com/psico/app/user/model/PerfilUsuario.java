package com.psico.app.user.model;

import com.psico.app.auth.model.Usuario;
import com.psico.app.emotion.model.TipoEmocion;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "perfiles_usuario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_emocional_actual")
    @Builder.Default
    private TipoEmocion estadoEmocionalActual = TipoEmocion.NEUTRAL;

    @Column(columnDefinition = "TEXT")
    private String preferencias;
}
