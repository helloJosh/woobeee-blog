package com.woobeee.blog.member.exception;

public class LoginRequestFormException extends RuntimeException{
    public LoginRequestFormException(String message) {
        super(message);
    }
}
