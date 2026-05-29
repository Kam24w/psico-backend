package com.psico.app.conversation.model;

import com.psico.app.emotion.model.EmotionType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contenido", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "raw_contenido", columnDefinition = "TEXT")
    private String rawContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "remitente")
    private Sender sender; // USER or AI

    @Column(name = "emocion_asociada")
    private com.psico.app.emotion.model.EmotionType associatedEmotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversacion_id", nullable = false)
    private Conversation conversation;

    @Column(name = "fecha")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<com.psico.app.ai.model.SecurityAlert> securityAlerts = new java.util.ArrayList<>();

    public enum Sender {
        USER, AI
    }
}
