package com.psico.app.ai.repository;

import com.psico.app.ai.model.ContextMemory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContextMemoryRepository extends JpaRepository<ContextMemory, Long> {
    List<ContextMemory> findByUserId(Long userId);
}
