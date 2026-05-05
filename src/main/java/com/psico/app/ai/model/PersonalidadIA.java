package com.psico.app.ai.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "personalidad_ia")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalidadIA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 2000)
    private String descripcion;

    @Column(name = "system_prompt", length = 5000)
    private String systemPrompt;

    private Double temperature;
    
    @Column(name = "max_tokens")
    private Integer maxTokens;

    @Builder.Default
    private boolean activa = true;
}
