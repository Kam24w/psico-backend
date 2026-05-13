package com.psico.app.intervention.repository;

import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.intervention.model.Recomendacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecomendacionRepository extends JpaRepository<Recomendacion, Long> {
    List<Recomendacion> findByEstadoInicialOrderByPrioridadAsc(TipoEmocion estadoInicial);
}
