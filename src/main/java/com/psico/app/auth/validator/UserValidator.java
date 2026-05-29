package com.psico.app.auth.validator;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.psico.app.auth.dto.AuthDTOs.RegisterRequest;

@Component
public class UserValidator {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

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

        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must have at least 8 characters");
        }

        if (password.length() > 20) {
            throw new IllegalArgumentException("Password cannot exceed 20 characters");
        }

        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }

        if (!Pattern.compile("[a-z]").matcher(password).find()) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }

        if (!Pattern.compile("[0-9]").matcher(password).find()) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }

        if (!Pattern.compile("[@$!%*?&._-]").matcher(password).find()) {
            throw new IllegalArgumentException("Password must contain at least one special character (@$!%*?&._-)");
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