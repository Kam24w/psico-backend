package com.psico.app.conversation.repository;

import com.psico.app.conversation.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c FROM Conversation c LEFT JOIN FETCH c.mensajes WHERE c.usuario.id = :usuarioId ORDER BY c.updatedAt DESC")
    List<Conversation> findByUsuarioIdOrderByUpdatedAtDesc(Long usuarioId);

    Optional<Conversation> findFirstByUsuarioIdAndActivaTrue(Long usuarioId);
    Optional<Conversation> findFirstByUsuarioIdAndActivaTrueAndTipo(Long usuarioId, String tipo);
}
