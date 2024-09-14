package com.woobeee.blog.post.exception;

public class CategoryCreateFromNotValidException extends RuntimeException{
    public CategoryCreateFromNotValidException(String message) {
        super(message);
    }
}
