package com.psico.app.ai.model;

import com.psico.app.conversation.model.Message;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feedback_respuesta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensaje_id", nullable = false)
    private Message message;

    @Column(name = "puntuacion", nullable = false)
    private Integer score; // 1-5 or thumbs up/down

    @Column(name = "comentario")
    private String comment;
}
