package com.woobeee.blog.post.exception;

public class CommentUpdateFromNotValidException extends RuntimeException{
    public CommentUpdateFromNotValidException(String message) {
        super(message);
    }
}
