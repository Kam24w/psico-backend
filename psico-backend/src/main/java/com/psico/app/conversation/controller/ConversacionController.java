package com.psico.app.conversation.controller;

import com.psico.app.conversation.model.Conversacion;
import com.psico.app.conversation.model.Mensaje;
import com.psico.app.conversation.service.ConversacionService;
import com.psico.app.emotion.model.TipoEmocion;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversacion")
@RequiredArgsConstructor
public class ConversacionController {

    private final ConversacionService conversacionService;

    /**
     * POST /api/conversacion/mensaje
     * Recibe el mensaje del usuario + emoción detectada por la cámara
     * Retorna la respuesta generada por la IA
     */
    @PostMapping("/mensaje")
    public ResponseEntity<Mensaje> enviarMensaje(@RequestBody MensajeRequest request) {
        Mensaje respuesta = conversacionService.procesarMensaje(
                request.getUsuarioId(),
                request.getContenido(),
                request.getEmocion()
        );
        return ResponseEntity.ok(respuesta);
    }

    /**
     * GET /api/conversacion/historial/{conversacionId}
     * Retorna todos los mensajes de una conversación
     */
    @GetMapping("/historial/{conversacionId}")
    public ResponseEntity<List<Mensaje>> obtenerHistorial(@PathVariable Long conversacionId) {
        return ResponseEntity.ok(conversacionService.obtenerHistorial(conversacionId));
    }

    /**
     * GET /api/conversacion/usuario/{usuarioId}
     * Retorna todas las conversaciones de un usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Conversacion>> obtenerConversaciones(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(conversacionService.obtenerConversacionesDeUsuario(usuarioId));
    }

    @Data
    public static class MensajeRequest {
        private Long usuarioId;
        private String contenido;
        private TipoEmocion emocion; // enviada por el frontend desde face-api.js
    }
}
