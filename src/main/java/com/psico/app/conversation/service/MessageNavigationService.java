package com.psico.app.conversation.service;

import com.psico.app.common.structures.MessageDoublyLinkedList;
import com.psico.app.conversation.dto.MensajeResponse;
import com.psico.app.conversation.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageNavigationService {

    private final ConversationService conversationService;

    public MessageNavigationState construirNavegacion(Long usuarioId) {
        List<Message> historial = conversationService.getActiveUserHistory(usuarioId);
        MessageDoublyLinkedList<Message> lista = new MessageDoublyLinkedList<>();
        historial.forEach(lista::add);

        return MessageNavigationState.builder()
                .totalMensajes(historial.size())
                .actual(historial.isEmpty() ? null : convertir(historial.get(0)))
                .tieneAnterior(false)
                .tieneSiguiente(historial.size() > 1)
                .build();
    }

    public MensajeResponse navegarAnterior(Long usuarioId) {
        return navegar(usuarioId, true);
    }

    public MensajeResponse navegarSiguiente(Long usuarioId) {
        return navegar(usuarioId, false);
    }

    private MensajeResponse navegar(Long usuarioId, boolean anterior) {
        List<Message> historial = conversationService.getActiveUserHistory(usuarioId);
        MessageDoublyLinkedList<Message> lista = new MessageDoublyLinkedList<>();
        historial.forEach(lista::add);

        if (historial.isEmpty()) {
            return null;
        }

        try {
            Message mensaje = anterior ? lista.previous() : lista.next();
            return convertir(mensaje);
        } catch (RuntimeException ex) {
            return convertir(historial.get(0));
        }
    }

    private MensajeResponse convertir(Message mensaje) {
        return MensajeResponse.builder()
                .id(mensaje.getId())
                .content(mensaje.getContenido())
                .sender(mensaje.getRemitente().name())
                .associatedEmotion(mensaje.getEmocionAsociada())
                .createdAt(mensaje.getFecha())
                .build();
    }

    @lombok.Builder
    @lombok.Data
    public static class MessageNavigationState {
        private int totalMensajes;
        private MensajeResponse actual;
        private boolean tieneAnterior;
        private boolean tieneSiguiente;
    }
}
