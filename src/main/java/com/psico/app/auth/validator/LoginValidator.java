package com.psico.app.auth.validator;

import org.springframework.stereotype.Component;

import com.psico.app.auth.dto.AuthDTOs.LoginRequest;
import com.psico.app.common.exception.ValidationException;

@Component
public class LoginValidator {

    public void validate(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ValidationException("VALIDATION_ERROR", "El email es obligatorio");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ValidationException("VALIDATION_ERROR", "La contraseña es obligatoria");
        }
    }
}
