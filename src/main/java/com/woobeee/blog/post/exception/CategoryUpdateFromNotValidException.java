package com.woobeee.blog.post.exception;

public class CategoryUpdateFromNotValidException extends RuntimeException{
    public CategoryUpdateFromNotValidException(String message) {
        super(message);
    }
}
