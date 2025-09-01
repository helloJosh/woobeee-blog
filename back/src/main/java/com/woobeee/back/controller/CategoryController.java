package com.woobeee.back.controller;


import com.woobeee.back.dto.request.PostCategoryRequest;
import com.woobeee.back.dto.response.ApiResponse;
import com.woobeee.back.dto.response.GetCategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/back/categories")
@Tag(name = "Category Controller", description = "카테고리 컨트롤러")
public interface CategoryController {
    @Operation(
            summary = "카테고리 저장 API"
    )
    @PostMapping("/{parentId}")
    ApiResponse<Void> saveCategory (
            @PathVariable(value = "parentId") Long parentId,
            @RequestBody PostCategoryRequest request
    );

    @Operation(
            summary = "카테고리 삭제 API"
    )
    @DeleteMapping("/{categoryId}")
    ApiResponse<Void> deleteCategory (
            @PathVariable(value = "categoryId") Long categoryId
    );

    @Operation(
            summary = "카테고리 전체 조회 API"
    )
    @GetMapping("")
    ApiResponse<GetCategoryResponse> getCategoryList(
            @RequestHeader(name = "Accept-Language", defaultValue = "ko-KR") String locale
    );
}
