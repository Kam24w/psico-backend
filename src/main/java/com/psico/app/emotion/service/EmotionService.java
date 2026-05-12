package com.psico.app.emotion.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.psico.app.auth.model.User;
import com.psico.app.emotion.model.Emotion;
import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.emotion.repository.EmotionRepository;
import com.psico.app.patterns.observer.NotificadorEmocion;
import com.psico.app.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmotionService {

    private final EmotionRepository emotionRepository;
    private final UserService usuarioService;
    private final NotificadorEmocion notificadorEmocion;

    public Emotion registerEmotion(Long userId, TipoEmocion emotionType, Double intensity) {
        User user = usuarioService.getById(userId);

        Emotion emotion = Emotion.builder()
                .tipo(emotionType)
                .intensidad(intensity)
                .user(user)
                .build();

        Emotion saved = emotionRepository.save(emotion);

        notificadorEmocion.notificar(userId, emotionType);

        log.info("Emotion registered successfully for userId: {}", userId);

        return saved;
    }

    public TipoEmocion getLatestEmotion(Long userId) {
        Emotion latest = emotionRepository.findLatestEmotionByUserId(userId);
        return latest != null ? latest.getTipo() : TipoEmocion.NEUTRAL;
    }

    public List<Emotion> getEmotionHistory(Long userId) {
        return emotionRepository.findByUserIdOrderByDetectedAtDesc(userId);
    }

    @Deprecated(forRemoval = false)
    public Emotion registrarEmocion(Long usuarioId, TipoEmocion tipo, Double intensidad) {
        return registerEmotion(usuarioId, tipo, intensidad);
    }

    @Deprecated(forRemoval = false)
    public TipoEmocion obtenerUltimaEmocion(Long usuarioId) {
        return getLatestEmotion(usuarioId);
    }

    @Deprecated(forRemoval = false)
    public List<Emotion> obtenerHistorial(Long usuarioId) {
        return getEmotionHistory(usuarioId);
    }
}