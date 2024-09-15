package com.woobeee.blog.member.exception;

public class LoginIdNotFoundException extends RuntimeException{
    public LoginIdNotFoundException(String message) {
        super(message);
    }
}
