package com.psico.app.memory.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.memory.model.MemoriaUsuario;
import com.psico.app.memory.repository.MemoriaUsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemoryService {

    private final MemoriaUsuarioRepository memoriaRepository;

    @Transactional
    public MemoriaUsuario saveMemory(Long userId, String content, TipoEmocion associatedEmotion) {
        MemoriaUsuario memory = MemoriaUsuario.builder()
                .usuarioId(userId)
                .texto(content)
                .emocionAsociada(associatedEmotion)
                .build();
        return memoriaRepository.save(memory);
    }

    public List<MemoriaUsuario> getMemories(Long userId) {
        return memoriaRepository.findByUsuarioIdOrderByCreadoEnDesc(userId);
    }

    @Deprecated(forRemoval = false)
    public MemoriaUsuario guardarMemoria(Long usuarioId, String texto, TipoEmocion emocion) {
        return saveMemory(usuarioId, texto, emocion);
    }

    @Deprecated(forRemoval = false)
    public List<MemoriaUsuario> obtenerMemorias(Long usuarioId) {
        return getMemories(usuarioId);
    }
}
