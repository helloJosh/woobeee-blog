package com.woobeee.back.exception;

public class CustomAuthenticationException extends RuntimeException{
    public CustomAuthenticationException(ErrorCode message) {
        super(message.name());
    }
}
