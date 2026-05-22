package com.psico.app.auth.validator;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.psico.app.auth.dto.AuthDTOs.RegisterRequest;

@Component
public class UserValidator {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    public void validarRegistro(RegisterRequest request) {
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());
        validateName(request.getName());
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (email.length() > 30) {
            throw new IllegalArgumentException("Email cannot exceed 30 characters");
        }

        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must have at least 6 characters");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (name.length() > 20) {
            throw new IllegalArgumentException("Name cannot exceed 20 characters");
        }
    }
}