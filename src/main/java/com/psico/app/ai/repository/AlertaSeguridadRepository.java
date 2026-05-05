package com.psico.app.ai.repository;

import com.psico.app.ai.model.AlertaSeguridad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertaSeguridadRepository extends JpaRepository<AlertaSeguridad, Long> {
    List<AlertaSeguridad> findByRevisadaFalse();
}
