package com.woobeee.blog.post.exception;

public class TagDoesNotExistException extends RuntimeException{
    public TagDoesNotExistException(String message) {
        super(message);
    }
}
