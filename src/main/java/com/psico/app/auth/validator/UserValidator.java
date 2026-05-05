package com.psico.app.auth.validator;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.psico.app.auth.dto.AuthDTOs.RegisterRequest;

@Component
public class UserValidator {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    public void validarRegistro(RegisterRequest request) {
        validarEmail(request.getEmail());
        validarPassword(request.getPassword());
        validarNombre(request.getNombre());
    }

    private void validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
    }

    private void validarPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
    }
}