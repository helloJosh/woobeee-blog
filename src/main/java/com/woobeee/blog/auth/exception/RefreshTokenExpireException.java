package com.woobeee.blog.auth.exception;

public class RefreshTokenExpireException extends RuntimeException{
    public RefreshTokenExpireException(String message) {
        super(message);
    }
}
