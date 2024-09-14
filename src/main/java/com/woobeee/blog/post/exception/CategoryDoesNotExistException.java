package com.woobeee.blog.post.exception;

public class CategoryDoesNotExistException extends RuntimeException{
    public CategoryDoesNotExistException(String message) {
        super(message);
    }
}
