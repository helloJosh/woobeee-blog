package com.woobeee.blog.post.exception;

public class CommentDoesNotExistException extends RuntimeException{
    public CommentDoesNotExistException(String message) {
        super(message);
    }
}
