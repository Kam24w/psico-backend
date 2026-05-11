package com.psico.app.common.exception;

public class ValidationException extends ApiException {

    public ValidationException(String code, String message) {
        super(code, message);
    }
}