package com.psico.app.user.service;

import com.psico.app.auth.model.User;
import com.psico.app.user.model.UserProfileEntity;
import com.psico.app.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserService userService;
    private final UserProfileRepository userProfileRepository;

    public UserProfile getProfile(Long userId) {
        User user = userService.getById(userId);
        UserProfileEntity profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserProfileEntity userProfile = new UserProfileEntity();
                    userProfile.setUser(user);
                    userProfile.setPreferencias("[]");
                    return userProfile;
                });

        String emotionalState = profile.getEstadoEmocionalActual() != null
                ? profile.getEstadoEmocionalActual().name()
                : "NEUTRAL";

        return UserProfile.builder()
                .userId(user.getId())
                .fullName(user.getNombre())
                .email(user.getEmail())
                .currentEmotionalState(emotionalState)
                .preferences(profile.getPreferencias())
                .build();
    }

    @Transactional
    public UserProfile updatePreferences(Long userId, String preferences) {
        User user = userService.getById(userId);
        UserProfileEntity profile = userProfileRepository.findByUserId(userId)
                .orElseGet(UserProfileEntity::new);
        profile.setPreferencias(preferences);
        profile.setUser(user);
        userProfileRepository.save(profile);

        return getProfile(userId);
    }

    @Deprecated(forRemoval = false)
    public UserProfile obtenerPerfil(Long usuarioId) {
        return getProfile(usuarioId);
    }

    @Deprecated(forRemoval = false)
    public UserProfile actualizarPreferencias(Long usuarioId, String preferencias) {
        return updatePreferences(usuarioId, preferencias);
    }

    @lombok.Builder
    @lombok.Data
    public static class UserProfile {
        private Long userId;
        private String fullName;
        private String email;
        private String currentEmotionalState;
        private String preferences;
    }
}
