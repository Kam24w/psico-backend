package com.psico.app.user.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psico.app.auth.model.User;
import com.psico.app.common.response.ApiResponse;
import com.psico.app.user.service.UserProfileService;
import com.psico.app.user.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService profileService;
    private final UserService userService;

    private void verifyOwnership(Long userId, Principal principal) {
        if (principal == null) throw new RuntimeException("Unauthorized");
        User user = userService.getByEmail(principal.getName());
        if (!user.getId().equals(userId)) {
            throw new RuntimeException("Access Denied: You cannot modify other users' profiles");
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileService.UserProfile>> getProfile(@PathVariable Long userId, Principal principal) {
        verifyOwnership(userId, principal);
        return ResponseEntity.ok(ApiResponse.success("Profile loaded", profileService.getProfile(userId)));
    }

    @PutMapping("/{userId}/preferences")
    public ResponseEntity<ApiResponse<UserProfileService.UserProfile>> updatePreferences(
            @PathVariable Long userId,
            @Valid @RequestBody PreferencesRequest request,
            Principal principal
    ) {
        verifyOwnership(userId, principal);
        return ResponseEntity.ok(ApiResponse.success("Preferences updated", profileService.updatePreferences(userId, request.getPreferences())));
    }

    @PutMapping("/{userId}/avatar")
    public ResponseEntity<ApiResponse<UserProfileService.UserProfile>> updateAvatar(
            @PathVariable Long userId,
            @Valid @RequestBody AvatarRequest request,
            Principal principal
    ) {
        verifyOwnership(userId, principal);
        return ResponseEntity.ok(ApiResponse.success("Avatar updated", profileService.updateAvatar(userId, request.getAvatarUrl())));
    }

    public static class PreferencesRequest {
        @NotBlank
        private String preferences;

        public String getPreferences() {
            return preferences;
        }

        public void setPreferences(String preferences) {
            this.preferences = preferences;
        }
    }

    public static class AvatarRequest {
        @NotBlank
        @com.fasterxml.jackson.annotation.JsonProperty("avatarUrl")
        private String avatarUrl;

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }
}
