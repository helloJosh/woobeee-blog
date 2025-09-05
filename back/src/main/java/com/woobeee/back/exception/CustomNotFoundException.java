package com.woobeee.back.exception;

public class CustomNotFoundException extends RuntimeException {
    public CustomNotFoundException(ErrorCode message) {
        super(message.name());
    }
}
