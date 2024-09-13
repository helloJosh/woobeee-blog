package com.woobeee.blog.post.exception;

public class PostTagDoesNotExistException extends RuntimeException{
    public PostTagDoesNotExistException(String message) {
        super(message);
    }
}
