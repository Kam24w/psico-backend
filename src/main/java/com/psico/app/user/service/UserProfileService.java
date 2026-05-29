package com.psico.app.user.service;

import com.psico.app.auth.model.User;
import com.psico.app.user.model.UserProfileEntity;
import com.psico.app.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
                .orElse(null);

        String emotionalState = (profile != null && profile.getCurrentEmotionalState() != null)
                ? profile.getCurrentEmotionalState().name()
                : "NEUTRAL";

        String preferences = (profile != null && profile.getPreferences() != null)
                ? profile.getPreferences()
                : "[]";

        String avatarUrl = (profile != null) ? profile.getAvatarUrl() : null;

        return UserProfile.builder()
                .userId(user.getId())
                .fullName(user.getName())
                .email(user.getEmail())
                .currentEmotionalState(emotionalState)
                .preferences(preferences)
                .avatarUrl(avatarUrl)
                .build();
    }

    @Transactional
    public UserProfile updatePreferences(Long userId, String preferences) {
        User user = userService.getById(userId);

        int updated = userProfileRepository.updatePreferencesByUserId(userId, preferences);

        if (updated == 0) {
            try {
                UserProfileEntity newProfile = new UserProfileEntity();
                newProfile.setUser(user);
                newProfile.setPreferences(preferences);
                newProfile.setCurrentEmotionalState(com.psico.app.emotion.model.EmotionType.NEUTRAL);
                userProfileRepository.save(newProfile);
            } catch (DataIntegrityViolationException e) {
                // If another thread inserted it, we just update it
                userProfileRepository.updatePreferencesByUserId(userId, preferences);
            }
        }

        return getProfile(userId);
    }

    @Transactional
    public UserProfile updateAvatar(Long userId, String avatarUrl) {
        User user = userService.getById(userId);

        int updated = userProfileRepository.updateAvatarByUserId(userId, avatarUrl);

        if (updated == 0) {
            try {
                UserProfileEntity newProfile = new UserProfileEntity();
                newProfile.setUser(user);
                newProfile.setPreferences("[]");
                newProfile.setAvatarUrl(avatarUrl);
                newProfile.setCurrentEmotionalState(com.psico.app.emotion.model.EmotionType.NEUTRAL);
                userProfileRepository.save(newProfile);
            } catch (DataIntegrityViolationException e) {
                // If another thread inserted it, we just update it
                userProfileRepository.updateAvatarByUserId(userId, avatarUrl);
            }
        }

        return getProfile(userId);
    }

    @lombok.Builder
    @lombok.Data
    public static class UserProfile {
        private Long userId;
        private String fullName;
        private String email;
        private String currentEmotionalState;
        private String preferences;
        private String avatarUrl;
    }
}
