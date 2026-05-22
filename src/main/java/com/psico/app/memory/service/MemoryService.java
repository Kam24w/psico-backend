package com.psico.app.memory.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psico.app.emotion.model.EmotionType;
import com.psico.app.memory.model.UserMemory;
import com.psico.app.memory.repository.UserMemoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemoryService {

    private final UserMemoryRepository memoryRepository;

    @Transactional
    public UserMemory saveMemory(Long userId, String content, EmotionType associatedEmotion) {
        UserMemory memory = UserMemory.builder()
                .userId(userId)
                .text(content)
                .associatedEmotion(associatedEmotion)
                .build();
        return memoryRepository.save(memory);
    }

    public List<UserMemory> getMemories(Long userId) {
        return memoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
