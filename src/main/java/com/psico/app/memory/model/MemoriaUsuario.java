package com.psico.app.memory.model;

import com.psico.app.emotion.model.TipoEmocion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "memorias_usuario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoriaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String texto;

    @Enumerated(EnumType.STRING)
    @Column(name = "emocion_asociada")
    private TipoEmocion emocionAsociada;

    @Column(name = "creado_en")
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();
}
