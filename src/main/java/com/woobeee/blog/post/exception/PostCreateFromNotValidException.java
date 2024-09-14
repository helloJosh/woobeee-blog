package com.woobeee.blog.post.exception;

public class PostCreateFromNotValidException extends RuntimeException{
    public PostCreateFromNotValidException(String message) {
        super(message);
    }
}
