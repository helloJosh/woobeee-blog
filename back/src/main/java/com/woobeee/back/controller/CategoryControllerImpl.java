package com.woobeee.back.controller;

import com.woobeee.back.dto.request.PostCategoryRequest;
import com.woobeee.back.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CategoryControllerImpl implements CategoryController {
    @Override
    public ApiResponse<Void> saveCategory(Long parentId, PostCategoryRequest request) {
        return null;
    }

    @Override
    public ApiResponse<Void> deleteCategory(Long categoryId) {
        return null;
    }
}
