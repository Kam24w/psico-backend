package com.psico.app.emotion.service;

import com.psico.app.auth.model.Usuario;
import com.psico.app.emotion.model.Emocion;
import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.emotion.repository.EmocionRepository;
import com.psico.app.patterns.observer.NotificadorEmocion;
import com.psico.app.user.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmocionService {

    private final EmocionRepository emocionRepository;
    private final UsuarioService usuarioService;
    private final NotificadorEmocion notificadorEmocion;

    @Transactional
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

        return guardada;
    }

    @Transactional(readOnly = true)
    public TipoEmocion obtenerUltimaEmocion(Long usuarioId) {
        Emocion ultima = emocionRepository.findUltimaEmocionByUsuarioId(usuarioId);
        return ultima != null ? ultima.getTipo() : TipoEmocion.NEUTRAL;
    }

    @Transactional(readOnly = true)
    public List<Emocion> obtenerHistorial(Long usuarioId) {
        return emocionRepository.findByUsuarioIdOrderByDetectedAtDesc(usuarioId);
    }
}
