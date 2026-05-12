package com.psico.app.intervention.model;

import com.psico.app.emotion.model.TipoEmocion;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recomendaciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recomendacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_inicial")
    private TipoEmocion estadoInicial;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Integer prioridad;
}
