package com.woobeee.back.exception;

public class CustomConflictException extends RuntimeException{
    public CustomConflictException(ErrorCode message) {
        super(message.name());
    }
}
