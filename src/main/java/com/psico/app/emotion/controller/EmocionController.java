package com.psico.app.emotion.controller;

import com.psico.app.emotion.model.Emocion;
import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.emotion.service.EmocionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emocion")
@RequiredArgsConstructor
public class EmocionController {

    private final EmocionService emocionService;

    @PostMapping
    public ResponseEntity<Emocion> registrarEmocion(@RequestBody EmocionRequest request) {
        Emocion emocion = emocionService.registrarEmocion(
                request.getUsuarioId(),
                request.getTipo(),
                request.getIntensidad()
        );
        return ResponseEntity.ok(emocion);
    }

    @GetMapping("/ultima/{usuarioId}")
    public ResponseEntity<TipoEmocion> obtenerUltima(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(emocionService.obtenerUltimaEmocion(usuarioId));
    }

    @GetMapping("/historial/{usuarioId}")
    public ResponseEntity<List<Emocion>> obtenerHistorial(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(emocionService.obtenerHistorial(usuarioId));
    }

    @Data
    public static class EmocionRequest {
        private Long usuarioId;
        private TipoEmocion tipo;
        private Double intensidad;
    }
}
