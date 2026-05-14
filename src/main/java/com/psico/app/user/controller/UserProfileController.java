package com.psico.app.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psico.app.common.response.ApiResponse;
import com.psico.app.user.service.UserProfileService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService profileService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileService.UserProfile>> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Profile loaded", profileService.getProfile(userId)));
    }

    @PutMapping("/{userId}/preferences")
    public ResponseEntity<ApiResponse<UserProfileService.UserProfile>> updatePreferences(
            @PathVariable Long userId,
            @Valid @RequestBody PreferencesRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Preferences updated", profileService.updatePreferences(userId, request.preferences())));
    }

    public static record PreferencesRequest(@NotBlank String preferences) {}
}
