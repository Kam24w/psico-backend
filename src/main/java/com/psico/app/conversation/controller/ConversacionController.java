package com.psico.app.conversation.controller;

import com.psico.app.conversation.dto.MensajeDTO;
import com.psico.app.conversation.model.Conversacion;
import com.psico.app.conversation.model.Mensaje;
import com.psico.app.conversation.service.ConversacionService;
import com.psico.app.emotion.model.TipoEmocion;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversacion")
@RequiredArgsConstructor
public class ConversacionController {

    private final ConversacionService conversacionService;

    /**
     * POST /api/conversacion/mensaje
     * Recibe el mensaje del usuario + emoción detectada por la cámara
     * Retorna la respuesta generada por la IA envuelta en un DTO
     */
    @PostMapping("/mensaje")
    public ResponseEntity<MensajeDTO> enviarMensaje(@RequestBody MensajeRequest request) {
        Mensaje respuesta = conversacionService.procesarMensaje(
                Objects.requireNonNull(request.getUsuarioId()),
                request.getContenido(),
                request.getEmocion()
        );
        return ResponseEntity.ok(convertToDTO(respuesta));
    }

    /**
     * GET /api/conversacion/historial/{conversacionId}
     * Retorna todos los mensajes de una conversación en formato DTO
     */
    @GetMapping("/historial/{conversacionId}")
    public ResponseEntity<List<MensajeDTO>> obtenerHistorial(@PathVariable @NonNull Long conversacionId) {
        List<Mensaje> historial = conversacionService.obtenerHistorial(conversacionId);
        return ResponseEntity.ok(historial.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    /**
     * GET /api/conversacion/usuario/{usuarioId}
     * Retorna todas las conversaciones de un usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Conversacion>> obtenerConversaciones(@PathVariable @NonNull Long usuarioId) {
        return ResponseEntity.ok(conversacionService.obtenerConversacionesDeUsuario(usuarioId));
    }

    private MensajeDTO convertToDTO(Mensaje mensaje) {
        return MensajeDTO.builder()
                .id(mensaje.getId())
                .contenido(mensaje.getContenido())
                .remitente(mensaje.getRemitente().name())
                .emocionAsociada(mensaje.getEmocionAsociada())
                .fecha(mensaje.getFecha())
                .build();
    }

    @Data
    public static class MensajeRequest {
        private Long usuarioId;
        private String contenido;
        private TipoEmocion emocion; // enviada por el frontend desde face-api.js
    }
}
