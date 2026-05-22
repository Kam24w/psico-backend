package com.psico.app.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.psico.app.auth.dto.AuthDTOs.AuthResponse;
import com.psico.app.auth.dto.AuthDTOs.LoginRequest;
import com.psico.app.auth.dto.AuthDTOs.RegisterRequest;
import com.psico.app.auth.model.Role;
import com.psico.app.auth.model.User;
import com.psico.app.auth.security.JwtUtil;
import com.psico.app.auth.validator.LoginValidator;
import com.psico.app.auth.validator.UserValidator;
import com.psico.app.common.exception.EmailAlreadyExistsException;
import com.psico.app.common.exception.UserNotFoundException;
import com.psico.app.common.exception.ValidationException;
import com.psico.app.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserValidator userValidator;
    private final LoginValidator loginValidator;

    // ===================== LOGIN =====================
    public AuthResponse login(LoginRequest request) {

        log.info("Login attempt for email: {}", request.getEmail());

        // 1. Validar datos
        loginValidator.validate(request);

        try {
            // 2. Autenticar con Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            log.error("Authentication failed for email: {}", request.getEmail());

            throw new ValidationException(
                    "INVALID_CREDENTIALS",
                    "Invalid credentials"
            );
        }

        // 3. Buscar usuario
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(
                        "USER_NOT_FOUND",
                        "User not found"
                ));

        // 4. Generar token
        String token = generateToken(user);

        log.info("Login successful for user: {}", user.getEmail());

        // 5. Respuesta
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getNombre())
                .email(user.getEmail())
                .rol(user.getRole().name())
                .build();
    }

    // ===================== REGISTER =====================
    public AuthResponse register(RegisterRequest request) {

        log.info("Register attempt for email: {}", request.getEmail());

        // 1. Validar datos
        userValidator.validarRegistro(request);

        // 2. Verificar email duplicado
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "EMAIL_ALREADY_EXISTS",
                    "Email is already registered"
            );
        }

        User user = User.builder()
                .nombre(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        String token = jwtUtil.generarToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getNombre())
                .email(user.getEmail())
                .rol(user.getRole().name())
                .build();
    }

    // ===================== UTIL =====================
    private String generateToken(User user) {
        return jwtUtil.generarToken(user.getEmail());
    }
}