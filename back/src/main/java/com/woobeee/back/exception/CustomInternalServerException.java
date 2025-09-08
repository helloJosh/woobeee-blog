package com.woobeee.back.exception;

public class CustomInternalServerException extends RuntimeException {
    public CustomInternalServerException(ErrorCode message) {
        super(message.name());
    }
}
