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
    private final RateLimiterService rateLimiterService;
    private final jakarta.servlet.http.HttpServletRequest httpServletRequest;

    private String decodeBase64(String encoded) {
        if (encoded == null) {
            return null;
        }
        try {
            return new String(java.util.Base64.getDecoder().decode(encoded), java.nio.charset.StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            log.warn("Password was not a valid Base64 string. Processing as plain text.");
            return encoded;
        }
    }

    private String getClientIp() {
        String xfHeader = httpServletRequest.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isBlank()) {
            return httpServletRequest.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    // ===================== LOGIN =====================
    public AuthResponse login(LoginRequest request) {

        String ip = getClientIp();
        log.info("Login attempt for email: {} from IP: {}", request.getEmail(), ip);

        // 1. Verificar Rate Limiting
        if (rateLimiterService.isBlocked(ip)) {
            log.warn("Blocked login attempt due to rate limiting for IP: {}", ip);
            throw new ValidationException(
                    "TOO_MANY_REQUESTS",
                    "Demasiados intentos fallidos. Tu IP está temporalmente bloqueada por 15 minutos."
            );
        }

        // 2. Decodificar contraseña
        String decodedPassword = decodeBase64(request.getPassword());
        request.setPassword(decodedPassword);

        // 3. Validar datos
        loginValidator.validate(request);

        try {
            // 4. Autenticar con Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException | org.springframework.security.authentication.InternalAuthenticationServiceException e) {
            log.error("Authentication failed for email: {} from IP: {}", request.getEmail(), ip);
            rateLimiterService.registerFailure(ip);

            throw new ValidationException(
                    "INVALID_CREDENTIALS",
                    "Invalid credentials"
            );
        }

        // 5. Buscar usuario
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(
                        "USER_NOT_FOUND",
                        "User not found"
                ));

        // 6. Limpiar intentos fallidos al tener éxito
        rateLimiterService.registerSuccess(ip);

        // 7. Generar token
        String token = generateToken(user);

        log.info("Login successful for user: {}", user.getEmail());

        // 8. Respuesta
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .rol(user.getRole().name())
                .build();
    }

    // ===================== REGISTER =====================
    public AuthResponse register(RegisterRequest request) {

        log.info("Register attempt for email: {}", request.getEmail());

        // 1. Decodificar contraseña
        String decodedPassword = decodeBase64(request.getPassword());
        request.setPassword(decodedPassword);

        // 2. Validar datos
        userValidator.validarRegistro(request);

        // 3. Verificar email duplicado
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "EMAIL_ALREADY_EXISTS",
                    "Email is already registered"
            );
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        String token = jwtUtil.generarToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .rol(user.getRole().name())
                .build();
    }

    // ===================== UTIL =====================
    private String generateToken(User user) {
        return jwtUtil.generarToken(user.getEmail());
    }
}