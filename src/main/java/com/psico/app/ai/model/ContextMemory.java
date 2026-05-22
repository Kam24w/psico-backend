package com.psico.app.ai.model;

import com.psico.app.auth.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "memoria_contexto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContextMemory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User user;

    @Column(name = "clave", columnDefinition = "TEXT")
    private String key;

    @Column(name = "valor", columnDefinition = "TEXT")
    private String value;

    @Column(name = "fecha_creacion")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
