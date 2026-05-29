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
                    userProfile.setPreferences("[]");
                    userProfile.setCurrentEmotionalState(com.psico.app.emotion.model.EmotionType.NEUTRAL);
                    return userProfile;
                });

        String emotionalState = profile.getCurrentEmotionalState() != null
                ? profile.getCurrentEmotionalState().name()
                : "NEUTRAL";

        return UserProfile.builder()
                .userId(user.getId())
                .fullName(user.getName())
                .email(user.getEmail())
                .currentEmotionalState(emotionalState)
                .preferences(profile.getPreferences())
                .build();
    }

    @Transactional
    public UserProfile updatePreferences(Long userId, String preferences) {
        User user = userService.getById(userId);
        UserProfileEntity profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserProfileEntity p = new UserProfileEntity();
                    p.setCurrentEmotionalState(com.psico.app.emotion.model.EmotionType.NEUTRAL);
                    return p;
                });
        profile.setPreferences(preferences);
        profile.setUser(user);
        userProfileRepository.save(profile);

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
    }
}
