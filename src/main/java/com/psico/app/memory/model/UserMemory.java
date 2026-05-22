package com.psico.app.memory.model;

import com.psico.app.emotion.model.EmotionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "memorias_usuario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMemory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long userId;

    @Column(name = "texto", columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(name = "emocion_asociada")
    private EmotionType associatedEmotion;

    @Column(name = "creado_en")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
