package com.psico.app.risk.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alertas_riesgo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel")
    private RiskLevel level;

    @Column(name = "razon", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "creado_en")
    private LocalDateTime createdAt;
}
