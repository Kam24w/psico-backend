package com.psico.app.analysis.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "analisis_emocional")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionalAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long userId;

    @Column(name = "mensajes_analizados")
    private Integer analyzedMessages;

    @Column(name = "positivos")
    private Integer positive;

    @Column(name = "negativos")
    private Integer negative;

    @Column(name = "fecha")
    private LocalDateTime createdAt;
}
