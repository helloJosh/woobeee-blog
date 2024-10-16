package com.woobeee.blog.member.exception;

public class LoginPasswordDoesNotMatchException extends RuntimeException{
    public LoginPasswordDoesNotMatchException(String message) {
        super(message);
    }
}
