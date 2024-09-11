package com.woobeee.blog.post.service.impl;

import com.woobeee.blog.post.dto.CategoryCreateRequest;
import com.woobeee.blog.post.dto.CategoryRequest;
import com.woobeee.blog.post.repository.CategoryRepository;
import com.woobeee.blog.post.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public void create(CategoryCreateRequest categoryCreateRequest) {
        List<CategoryRequest> categoryRequests = categoryCreateRequest.categories();

        for (CategoryRequest categoryRequest : categoryRequests) {

        }
    }

    @Override
    public Long delete(Long categoryId) {
        return 0L;
    }

    @Override
    public Long update(CategoryRequest categoryRequest) {
        return 0L;
    }

    @Override
    public Long read(Long categoryId) {
        return 0L;
    }
}
