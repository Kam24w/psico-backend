package com.psico.app.emotion.repository;

import com.psico.app.emotion.model.Emocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmocionRepository extends JpaRepository<Emocion, Long> {

    List<Emocion> findByUsuarioIdOrderByDetectedAtDesc(Long usuarioId);

    @Query("SELECT e FROM Emocion e WHERE e.usuario.id = :usuarioId ORDER BY e.detectedAt DESC LIMIT 1")
    Emocion findUltimaEmocionByUsuarioId(Long usuarioId);
}
