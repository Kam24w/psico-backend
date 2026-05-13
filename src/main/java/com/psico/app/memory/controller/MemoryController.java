package com.psico.app.memory.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.psico.app.common.response.ApiResponse;
import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.memory.model.MemoriaUsuario;
import com.psico.app.memory.service.MemoryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/api/memories", "/api/memoria"})
@RequiredArgsConstructor
public class MemoryController {

    private final MemoryService memoryService;

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<MemoriaUsuario>> saveMemory(
            @PathVariable Long userId,
            @Valid @RequestBody MemoryRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Memory saved",
                memoryService.saveMemory(userId, request.content(), request.associatedEmotion())));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<MemoriaUsuario>>> getMemories(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Memories retrieved", memoryService.getMemories(userId)));
    }

    public record MemoryRequest(
            @NotBlank @JsonAlias("texto") String content,
            @NotNull @JsonAlias("emocionAsociada") TipoEmocion associatedEmotion
    ) {}
}
