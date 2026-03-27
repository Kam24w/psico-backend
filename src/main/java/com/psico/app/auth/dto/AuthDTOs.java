package com.psico.app.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class AuthDTOs {

    @Data
    public static class LoginRequest {
        @Email(message = "Email inválido")
        @NotBlank(message = "El email es requerido")
        private String email;

        @NotBlank(message = "La contraseña es requerida")
        private String password;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "El nombre es requerido")
        private String nombre;

        @Email(message = "Email inválido")
        @NotBlank(message = "El email es requerido")
        private String email;

        @NotBlank(message = "La contraseña es requerida")
        private String password;
    }

    @Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class AuthResponse {
        private String token;
        private Long usuarioId;
        private String nombre;
        private String email;
        private String rol;
    }
}
