package com.psico.app.conversation.model;

import com.psico.app.emotion.model.TipoEmocion;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenido;

    @Enumerated(EnumType.STRING)
    @Column(name = "remitente")
    private Remitente remitente; // USER o AI

    @Enumerated(EnumType.STRING)
    @Column(name = "emocion_asociada")
    private TipoEmocion emocionAsociada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversacion_id", nullable = false)
    private Conversacion conversacion;

    @Column(name = "fecha")
    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();

    public enum Remitente {
        USER, AI
    }
}
