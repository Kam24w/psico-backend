package com.psico.app.intervention.service;

import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.intervention.model.Recomendacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TherapeuticService {

    private final RecommendationService recommendationService;

    public List<Recomendacion> sugerirEjercicios(TipoEmocion emocion) {
        return recommendationService.obtenerRecomendaciones(emocion);
    }
}
