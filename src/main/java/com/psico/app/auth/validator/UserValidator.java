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
            throw new IllegalArgumentException("El correo electrónico es requerido");
        }

        if (email.length() > 30) {
            throw new IllegalArgumentException("El correo electrónico no puede superar los 30 caracteres");
        }

        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new IllegalArgumentException("Formato de correo electrónico inválido");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña es requerida");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }

        if (password.length() > 20) {
            throw new IllegalArgumentException("La contraseña no puede superar los 20 caracteres");
        }

        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra mayúscula");
        }

        if (!Pattern.compile("[a-z]").matcher(password).find()) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra minúscula");
        }

        if (!Pattern.compile("[0-9]").matcher(password).find()) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un número");
        }

        if (!Pattern.compile("[@$!%*?&._-]").matcher(password).find()) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un carácter especial (@$!%*?&._-)");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }

        if (name.length() > 100) {
            throw new IllegalArgumentException("El nombre no puede superar los 100 caracteres");
        }
    }
}