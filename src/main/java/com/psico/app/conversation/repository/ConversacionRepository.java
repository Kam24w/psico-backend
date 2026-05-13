package com.psico.app.conversation.repository;

import com.psico.app.conversation.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversacionRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByUsuarioIdOrderByUpdatedAtDesc(Long usuarioId);
    Optional<Conversation> findFirstByUsuarioIdAndActivaTrue(Long usuarioId);
}
