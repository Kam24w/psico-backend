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
public class AnalisisEmocional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "mensajes_analizados")
    private Integer mensajesAnalizados;

    @Column(name = "positivos")
    private Integer positivos;

    @Column(name = "negativos")
    private Integer negativos;

    @Column(name = "fecha")
    private LocalDateTime fecha;
}
