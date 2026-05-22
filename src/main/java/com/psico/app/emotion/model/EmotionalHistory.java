package com.psico.app.emotion.model;

import com.psico.app.auth.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "historial_emocional")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User user;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "historial_id")
    @Builder.Default
    private List<Emotion> emotions = new ArrayList<>();

    public void addEmotion(Emotion emotion) {
        this.emotions.add(emotion);
    }

    public EmotionType getLatestEmotion() {
        if (emotions.isEmpty()) return EmotionType.NEUTRAL;
        return emotions.get(emotions.size() - 1).getType();
    }
}
