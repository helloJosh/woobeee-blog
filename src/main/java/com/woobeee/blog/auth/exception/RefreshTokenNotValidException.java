package com.woobeee.blog.auth.exception;

public class RefreshTokenNotValidException extends RuntimeException{
    public RefreshTokenNotValidException(String message) {
        super(message);
    }
}
