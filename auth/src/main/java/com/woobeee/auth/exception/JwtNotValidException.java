package com.woobeee.auth.exception;

public class JwtNotValidException extends RuntimeException {
    public JwtNotValidException(String message) {
        super(message);
    }
}
