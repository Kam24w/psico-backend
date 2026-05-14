package com.psico.app.intervention.controller;

import com.psico.app.common.response.ApiResponse;
import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.intervention.model.Recomendacion;
import com.psico.app.intervention.service.TherapeuticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terapia")
@RequiredArgsConstructor
public class TherapeuticController {

    private final TherapeuticService therapeuticService;

    @GetMapping("/ejercicios")
    public ResponseEntity<ApiResponse<List<Recomendacion>>> sugerirEjercicios(@RequestParam TipoEmocion emocion) {
        return ResponseEntity.ok(ApiResponse.success("Ejercicios sugeridos", therapeuticService.sugerirEjercicios(emocion)));
    }
}
