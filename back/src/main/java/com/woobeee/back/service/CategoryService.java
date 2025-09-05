package com.woobeee.back.service;

import com.woobeee.back.dto.request.PostCategoryRequest;
import com.woobeee.back.dto.response.GetCategoryResponse;

import java.util.List;

public interface CategoryService {
    void saveCategory(PostCategoryRequest request, Long parentId);
    void deleteCategory(Long categoryId);
    List<GetCategoryResponse> getCategoryList(String locale);
}
