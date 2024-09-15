package com.woobeee.blog.post.exception;

public class CommentCreateFromNotValidException extends RuntimeException{
    public CommentCreateFromNotValidException(String message) {
        super(message);
    }
}
