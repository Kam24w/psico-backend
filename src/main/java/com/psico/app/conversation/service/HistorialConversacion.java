package com.psico.app.conversation.service;

import com.psico.app.conversation.model.Conversation;
import com.psico.app.conversation.model.Message;
import com.psico.app.conversation.repository.ConversacionRepository;
import com.psico.app.conversation.repository.MensajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * HistorialConversacion
 * Gestiona el acceso al historial completo de chats del usuario.
 */
@Service
@RequiredArgsConstructor
public class HistorialConversacion {

    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;

    public List<Conversation> obtenerTodasLasConversaciones(@NonNull Long usuarioId) {
        return conversacionRepository.findByUsuarioIdOrderByUpdatedAtDesc(usuarioId);
    }

    public List<Message> obtenerMensajesDeConversacion(@NonNull Long conversacionId) {
        return mensajeRepository.findByConversationIdOrderByFechaAsc(conversacionId);
    }

    @Transactional
    public void cerrarConversacion(@NonNull Long conversacionId) {
        conversacionRepository.findById(conversacionId).ifPresent(conv -> {
            conv.setActiva(false);
            conversacionRepository.save(Objects.requireNonNull(conv));
        });
    }
}
