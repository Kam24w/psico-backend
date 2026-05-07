package com.psico.app.emotion.controller;

import com.psico.app.emotion.model.Emocion;
import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.emotion.service.EmocionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/emocion")
@RequiredArgsConstructor
public class EmocionController {

    private final EmocionService emocionService;

    @PostMapping
    public ResponseEntity<Emocion> registrarEmocion(@RequestBody EmocionRequest request) {
        Emocion emocion = emocionService.registrarEmocion(
                Objects.requireNonNull(request.getUsuarioId()),
                request.getTipo(),
                request.getIntensidad()
        );
        return ResponseEntity.ok(emocion);
    }

    @GetMapping("/ultima/{usuarioId}")
    public ResponseEntity<TipoEmocion> obtenerUltima(@PathVariable @NonNull Long usuarioId) {
        return ResponseEntity.ok(emocionService.obtenerUltimaEmocion(usuarioId));
    }

    @GetMapping("/historial/{usuarioId}")
    public ResponseEntity<List<Emocion>> obtenerHistorial(@PathVariable @NonNull Long usuarioId) {
        return ResponseEntity.ok(emocionService.obtenerHistorial(usuarioId));
    }

    @Data
    public static class EmocionRequest {
        private Long usuarioId;
        private TipoEmocion tipo;
        private Double intensidad;
    }
}
