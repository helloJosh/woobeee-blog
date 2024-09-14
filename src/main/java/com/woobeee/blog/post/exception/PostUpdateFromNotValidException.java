package com.woobeee.blog.post.exception;

public class PostUpdateFromNotValidException extends RuntimeException{
    public PostUpdateFromNotValidException(String message) {
        super(message);
    }
}
