package com.psico.app.ai.repository;

import com.psico.app.ai.model.MemoriaContexto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MemoriaContextoRepository extends JpaRepository<MemoriaContexto, Long> {
    List<MemoriaContexto> findByUsuarioId(Long usuarioId);
}
