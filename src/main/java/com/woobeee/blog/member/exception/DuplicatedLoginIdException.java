package com.woobeee.blog.member.exception;

public class DuplicatedLoginIdException extends RuntimeException{
    public DuplicatedLoginIdException(String message) {
        super(message);
    }
}
