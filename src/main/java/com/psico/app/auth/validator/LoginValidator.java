package com.psico.app.auth.validator;

import org.springframework.stereotype.Component;

import com.psico.app.auth.dto.AuthDTOs.LoginRequest;
import com.psico.app.common.exception.ValidationException;

@Component
public class LoginValidator {

    public void validate(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ValidationException("VALIDATION_ERROR", "Email is required");
        }

        if (request.getEmail().length() > 30) {
            throw new ValidationException("VALIDATION_ERROR", "Email cannot exceed 30 characters");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ValidationException("VALIDATION_ERROR", "Password is required");
        }
    }
}
