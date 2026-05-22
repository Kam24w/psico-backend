package com.psico.app.ai.model;

import com.psico.app.auth.model.User;
import com.psico.app.conversation.model.Message;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerta_seguridad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensaje_id")
    private Message message;

    @Column(name = "tipo", nullable = false)
    private String type; // ej: "SUICIDIO", "AUTOLESION", "VIOLENCIA"

    @Column(name = "nivel_riesgo", nullable = false)
    private Integer riskLevel; // 1-10

    @Column(name = "fragmento_detectado", columnDefinition = "TEXT")
    private String detectedSnippet;

    @Column(name = "fecha_deteccion")
    private LocalDateTime detectedAt;

    @Builder.Default
    private boolean reviewed = false;

    @PrePersist
    protected void onCreate() {
        detectedAt = LocalDateTime.now();
    }
}
