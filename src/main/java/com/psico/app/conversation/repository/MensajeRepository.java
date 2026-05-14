package com.psico.app.conversation.repository;

import com.psico.app.conversation.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderByFechaAsc(Long conversationId);
}
