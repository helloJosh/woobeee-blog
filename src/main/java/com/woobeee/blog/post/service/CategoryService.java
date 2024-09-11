package com.woobeee.blog.post.service;

import com.woobeee.blog.post.dto.CategoryCreateRequest;
import com.woobeee.blog.post.dto.CategoryRequest;
import com.woobeee.blog.post.dto.PostUpdateRequest;

public interface CategoryService {
    void create(CategoryCreateRequest categoryCreateRequest);
    Long delete(Long categoryId);
    Long update(CategoryRequest categoryRequest);
    Long read(Long categoryId);
}
