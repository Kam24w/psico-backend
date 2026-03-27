package com.psico.app.conversation.repository;

import com.psico.app.conversation.model.Conversacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversacionRepository extends JpaRepository<Conversacion, Long> {
    List<Conversacion> findByUsuarioIdOrderByUpdatedAtDesc(Long usuarioId);
    Optional<Conversacion> findFirstByUsuarioIdAndActivaTrue(Long usuarioId);
}
