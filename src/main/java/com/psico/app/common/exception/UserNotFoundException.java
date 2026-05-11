package com.psico.app.common.exception;

public class UserNotFoundException extends ApiException {

    public UserNotFoundException(String code, String message) {
        super(code, message);
    }
}