package com.woobeee.blog.post.controller;

import com.woobeee.blog.api.Response;
import com.woobeee.blog.post.dto.request.CategoryCreateRequest;
import com.woobeee.blog.post.dto.request.CategoryUpdateRequest;
import com.woobeee.blog.post.dto.response.CategoryReadAllResponse;
import com.woobeee.blog.post.exception.CategoryCreateFromNotValidException;
import com.woobeee.blog.post.exception.CategoryUpdateFromNotValidException;
import com.woobeee.blog.post.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/blog")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/categories")
    public Response<Void> saveCategory(
            @Valid @RequestBody CategoryCreateRequest categoryCreateRequest,
            BindingResult bindingResult
            ){
        if (bindingResult.hasErrors()) {
            throw new CategoryCreateFromNotValidException("카테고리 폼이 유효하지 않습니다.");
        }

        categoryService.create(categoryCreateRequest);

        return Response.createSuccess();
    }

    @GetMapping("/categories")
    public Response<CategoryReadAllResponse> readCategories(){
        return Response.success(categoryService.readAll());
    }

    @PutMapping("/categories/{categoryId}")
    public Response<Void> updatedCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryUpdateRequest categoryUpdateRequest,
            BindingResult bindingResult
            ) {
        if (bindingResult.hasErrors()) {
            throw new CategoryUpdateFromNotValidException("카테고리 폼이 유효하지 않습니다.");
        }



        categoryService.update(categoryUpdateRequest);

        return Response.success();
    }

    @DeleteMapping("/categories/{categoryId}")
    public Response<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.delete(categoryId);

        return Response.deleteSuccess();
    }
}
