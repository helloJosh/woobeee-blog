package com.woobeee.blog.auth.exception;

public class AuthenticationInvalidException extends RuntimeException{
    public AuthenticationInvalidException(String message) {
        super(message);
    }
}
