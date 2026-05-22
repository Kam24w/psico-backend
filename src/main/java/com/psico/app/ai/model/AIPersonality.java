package com.psico.app.ai.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "personalidad_ia")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIPersonality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String name;

    @Column(name = "descripcion", length = 2000)
    private String description;

    @Column(name = "system_prompt", length = 5000)
    private String systemPrompt;

    private Double temperature;
    
    @Column(name = "max_tokens")
    private Integer maxTokens;

    @Builder.Default
    @Column(name = "activa")
    private boolean active = true;
}
