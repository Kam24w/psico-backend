package com.psico.app.ai.model;

import com.psico.app.conversation.model.Mensaje;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feedback_respuesta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackRespuesta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensaje_id", nullable = false)
    private Mensaje mensaje;

    @Column(nullable = false)
    private Integer puntuacion; // 1-5 o pulgar arriba/abajo

    private String comentario;
}
