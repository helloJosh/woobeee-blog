package com.woobeee.back.controller;

import com.woobeee.back.aop.Idempotent;
import com.woobeee.back.dto.request.PostCategoryRequest;
import com.woobeee.back.dto.response.ApiResponse;
import com.woobeee.back.dto.response.GetCategoryResponse;
import com.woobeee.back.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CategoryControllerImpl implements CategoryController {
    private final CategoryService categoryService;

    @Override
    @Idempotent
    public ApiResponse<Void> saveCategory (
            Long parentId,
            PostCategoryRequest request
    ) {
        return null;
    }

    @Override
    public ApiResponse<Void> deleteCategory (
            Long categoryId
    ) {
        return null;
    }

    @Override
    public ApiResponse<List<GetCategoryResponse>> getCategoryList (
            String locale
    ) {
        log.info("get category list");

        return ApiResponse.success(
                categoryService.getCategoryList(locale),
                "success"
        );
    }
}
