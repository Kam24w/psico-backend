package com.psico.app.memory.repository;

import com.psico.app.memory.model.UserMemory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMemoryRepository extends JpaRepository<UserMemory, Long> {
    List<UserMemory> findByUserIdOrderByCreatedAtDesc(Long userId);
}
