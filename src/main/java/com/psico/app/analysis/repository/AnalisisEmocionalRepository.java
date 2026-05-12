package com.psico.app.analysis.repository;

import com.psico.app.analysis.model.AnalisisEmocional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalisisEmocionalRepository extends JpaRepository<AnalisisEmocional, Long> {
    List<AnalisisEmocional> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
}
