package com.woobeee.blog.member.exception;

public class MemberDoesNotExistException extends RuntimeException{
    public MemberDoesNotExistException(String message) {
        super(message);
    }
}
