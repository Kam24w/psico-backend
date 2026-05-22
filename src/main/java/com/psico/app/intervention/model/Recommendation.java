package com.psico.app.intervention.model;

import com.psico.app.emotion.model.EmotionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recomendaciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_inicial")
    private EmotionType initialState;

    @Column(name = "titulo", nullable = false)
    private String title;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String description;

    @Column(name = "prioridad", nullable = false)
    private Integer priority;
}
