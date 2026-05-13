package com.psico.app;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.psico.app.common.exception.ApiException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(errorBody(ex.getCode(), ex.getMessage(), HttpStatus.BAD_REQUEST.value(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
    String firstError = ex.getBindingResult().getFieldErrors().stream()
        .findFirst()
        .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
        .orElse("Validation failed");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(errorBody("VALIDATION_ERROR", firstError, HttpStatus.BAD_REQUEST.value(), null));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(errorBody("RUNTIME_ERROR", ex.getMessage(), HttpStatus.BAD_REQUEST.value(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorBody("INTERNAL_SERVER_ERROR", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
    }

    private Map<String, Object> errorBody(String code, String message, int status, String detail) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("success", false);
    body.put("code", code);
    body.put("status", status);
    body.put("message", message);
    if (detail != null) {
        body.put("detail", detail);
    }
    body.put("timestamp", LocalDateTime.now());
    return body;
    }
}