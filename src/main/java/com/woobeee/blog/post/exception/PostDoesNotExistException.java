package com.woobeee.blog.post.exception;

public class PostDoesNotExistException extends RuntimeException{
    public PostDoesNotExistException(String message) {
        super(message);
    }
}
