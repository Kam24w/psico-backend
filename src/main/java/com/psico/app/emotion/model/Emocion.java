package com.psico.app.emotion.model;

import com.psico.app.auth.model.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "emociones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Emocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEmocion tipo;

    @NotNull
    @Min(0)
    @Max(1)
    @Column(nullable = false)
    private Double intensidad;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "detected_at")
    @Builder.Default
    private LocalDateTime detectedAt = LocalDateTime.now();
}