package com.psico.app.risk.repository;

import com.psico.app.risk.model.AlertaRiesgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRiesgoRepository extends JpaRepository<AlertaRiesgo, Long> {
    List<AlertaRiesgo> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);
}
