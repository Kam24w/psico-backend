package com.psico.app.common.exception;

public class EmailAlreadyExistsException extends ApiException {

    public EmailAlreadyExistsException(String code, String message) {
        super(code, message);
    }
}