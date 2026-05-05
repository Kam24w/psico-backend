package com.psico.app.ai.model;

import com.psico.app.auth.model.Usuario;
import com.psico.app.conversation.model.Mensaje;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerta_seguridad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertaSeguridad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensaje_id")
    private Mensaje mensaje;

    @Column(nullable = false)
    private String tipo; // ej: "SUICIDIO", "AUTOLESION", "VIOLENCIA"

    @Column(nullable = false)
    private Integer nivelRiesgo; // 1-10

    @Column(columnDefinition = "TEXT")
    private String fragmentoDetectado;

    private LocalDateTime fechaDeteccion;

    @Builder.Default
    private boolean revisada = false;

    @PrePersist
    protected void onCreate() {
        fechaDeteccion = LocalDateTime.now();
    }
}
