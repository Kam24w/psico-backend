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
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmocionService {

    private final EmocionRepository emocionRepository;
    private final UsuarioService usuarioService;
    private final NotificadorEmocion notificadorEmocion;
    private final EmotionValidator emotionValidator;

    // ===================== REGISTRAR =====================
    public Emocion registrarEmocion(Long usuarioId, TipoEmocion tipo, Double intensidad) {

        log.info("Registering emotion for userId: {}", usuarioId);

        // 1. Validar datos
        emotionValidator.validate(tipo, intensidad);

        // 2. Buscar usuario
        Usuario usuario = usuarioService.buscarPorId(usuarioId);

        // 3. Crear emoción
        Emocion emocion = Emocion.builder()
                .tipo(tipo)
                .intensidad(intensidad)
                .usuario(usuario)
                .build();

        // 4. Guardar emoción
        Emocion guardada;
        try {
            guardada = emocionRepository.save(emocion);
        } catch (Exception e) {
            log.error("Error saving emotion for userId: {}", usuarioId);

            throw new ValidationException(
                    "EMOTION_SAVE_ERROR",
                    "Error al guardar la emoción"
            );
        }

        // 5. Notificar (Observer Pattern)
        notificadorEmocion.notificar(usuarioId, tipo);

        log.info("Emotion registered successfully for userId: {}", usuarioId);

        return guardada;
    }

    // ===================== ÚLTIMA EMOCIÓN =====================
    public TipoEmocion obtenerUltimaEmocion(Long usuarioId) {

        log.info("Fetching last emotion for userId: {}", usuarioId);

        Emocion ultima = emocionRepository.findUltimaEmocionByUsuarioId(usuarioId);

        if (ultima == null) {
            return TipoEmocion.NEUTRAL;
        }

        return ultima.getTipo();
    }

    // ===================== HISTORIAL =====================
    public List<Emocion> obtenerHistorial(Long usuarioId) {

        log.info("Fetching emotion history for userId: {}", usuarioId);

        return emocionRepository.findByUsuarioIdOrderByDetectedAtDesc(usuarioId);
    }
}