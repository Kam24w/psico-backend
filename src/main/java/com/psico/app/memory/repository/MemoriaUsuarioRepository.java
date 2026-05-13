package com.psico.app.memory.repository;

import com.psico.app.memory.model.MemoriaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoriaUsuarioRepository extends JpaRepository<MemoriaUsuario, Long> {
    List<MemoriaUsuario> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);
}
