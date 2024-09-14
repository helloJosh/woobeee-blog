package com.woobeee.blog.post.exception;

public class PostCategoryDoesNotExistException extends RuntimeException{
    public PostCategoryDoesNotExistException(String message) {
        super(message);
    }
}
