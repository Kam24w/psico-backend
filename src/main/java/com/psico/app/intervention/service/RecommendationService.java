package com.psico.app.intervention.service;

import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.intervention.model.Recomendacion;
import com.psico.app.intervention.repository.RecomendacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecomendacionRepository recomendacionRepository;

    public List<Recomendacion> obtenerRecomendaciones(TipoEmocion emocion) {
        return recomendacionRepository.findByEstadoInicialOrderByPrioridadAsc(emocion);
    }

    @Transactional
    public Recomendacion guardarRecomendacion(Recomendacion recomendacion) {
        return recomendacionRepository.save(recomendacion);
    }
}
