package com.psico.app.conversation.repository;

import com.psico.app.conversation.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c FROM Conversation c LEFT JOIN FETCH c.messages WHERE c.user.id = :userId ORDER BY c.updatedAt DESC")
    List<Conversation> findByUserIdOrderByUpdatedAtDesc(Long userId);

    Optional<Conversation> findFirstByUserIdAndActiveTrue(Long userId);
    Optional<Conversation> findFirstByUserIdAndActiveTrueAndType(Long userId, String type);
}
