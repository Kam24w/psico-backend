package com.psico.app.emotion.service;

import com.psico.app.auth.model.Usuario;
import com.psico.app.common.exception.ValidationException;
import com.psico.app.emotion.model.Emocion;
import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.emotion.repository.EmocionRepository;
import com.psico.app.emotion.validator.EmotionValidator;
import com.psico.app.patterns.observer.NotificadorEmocion;
import com.psico.app.user.service.UsuarioService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmocionService {

    private final EmocionRepository emocionRepository;
    private final UsuarioService usuarioService;
    private final NotificadorEmocion notificadorEmocion;
    private final EmotionValidator emotionValidator;

    public Emocion registrarEmocion(Long usuarioId, TipoEmocion tipo, Double intensidad) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);

        Emocion emocion = Emocion.builder()
                .tipo(tipo)
                .intensidad(intensidad)
                .usuario(usuario)
                .build();

        Emocion guardada = emocionRepository.save(emocion);

        // Patrón Observer: notificar cambio emocional
        notificadorEmocion.notificar(usuarioId, tipo);

        log.info("Emotion registered successfully for userId: {}", usuarioId);

        return guardada;
    }

    public TipoEmocion obtenerUltimaEmocion(Long usuarioId) {
        Emocion ultima = emocionRepository.findUltimaEmocionByUsuarioId(usuarioId);
        return ultima != null ? ultima.getTipo() : TipoEmocion.NEUTRAL;
    }

    public List<Emocion> obtenerHistorial(Long usuarioId) {
        return emocionRepository.findByUsuarioIdOrderByDetectedAtDesc(usuarioId);
    }
}