package com.psico.app.intervention.controller;

import com.psico.app.common.response.ApiResponse;
import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.intervention.model.Recomendacion;
import com.psico.app.intervention.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/intervencion")
@RequiredArgsConstructor
public class InterventionController {

    private final RecommendationService recommendationService;

    @GetMapping("/{emocion}")
    public ResponseEntity<ApiResponse<List<Recomendacion>>> obtenerRecomendaciones(@PathVariable TipoEmocion emocion) {
        return ResponseEntity.ok(ApiResponse.success("Recomendaciones cargadas", recommendationService.obtenerRecomendaciones(emocion)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Recomendacion>> crearRecomendacion(@RequestBody Recomendacion recomendacion) {
        return ResponseEntity.ok(ApiResponse.success("Recomendación guardada", recommendationService.guardarRecomendacion(recomendacion)));
    }
}
