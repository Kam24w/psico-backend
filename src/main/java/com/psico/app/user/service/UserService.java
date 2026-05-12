package com.psico.app.user.service;

import com.psico.app.auth.model.User;
import com.psico.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository usuarioRepository;

    public User getByEmail(@NonNull String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    public User getById(@NonNull Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Deprecated(forRemoval = false)
    public User buscarPorEmail(@NonNull String email) {
        return getByEmail(email);
    }

    @Deprecated(forRemoval = false)
    public User buscarPorId(@NonNull Long id) {
        return getById(id);
    }
}
