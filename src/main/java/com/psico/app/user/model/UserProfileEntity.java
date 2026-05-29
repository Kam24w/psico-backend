package com.psico.app.user.model;

import com.psico.app.auth.model.User;
import com.psico.app.emotion.model.EmotionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "perfiles_usuario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // usuarioId is mapped via the User entity above

    @Builder.Default
    @Column(name = "estado_emocional_actual")
    private EmotionType currentEmotionalState = EmotionType.NEUTRAL;

    @Column(name = "preferencias", columnDefinition = "TEXT")
    private String preferences;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;
}