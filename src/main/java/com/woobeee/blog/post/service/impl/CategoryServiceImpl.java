package com.woobeee.blog.post.service.impl;

import com.woobeee.blog.post.dto.CategoryCreateRequest;
import com.woobeee.blog.post.dto.CategoryRequest;
import com.woobeee.blog.post.dto.CategoryUpdateRequest;
import com.woobeee.blog.post.dto.response.CategoryReadAllResponse;
import com.woobeee.blog.post.dto.response.CategoryResponse;
import com.woobeee.blog.post.entity.Category;
import com.woobeee.blog.post.exception.CategoryDoesNotExistException;
import com.woobeee.blog.post.repository.CategoryRepository;
import com.woobeee.blog.post.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 카테고리 서비스 구현체.
 *
 * @author 김병우
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(CategoryCreateRequest categoryCreateRequest) {
        List<CategoryRequest> categoryRequests = categoryCreateRequest.categories();

        for (CategoryRequest categoryRequest : categoryRequests) {
            Category parent = saveCategory(null, categoryRequest);
        }
    }

    /**
     * 카테고리 생성 재귀 메소드.
     *
     */
    private Category saveCategory(Category parent, CategoryRequest categoryRequest) {
        Category category = new Category(categoryRequest.name());
        category.setParent(parent);

        if (!categoryRepository.existsCategoryByName(categoryRequest.name())) {
            categoryRepository.save(category);
        }

        if (categoryRequest.children() != null
            && !categoryRequest.children().isEmpty()) {
            for (CategoryRequest childRequest : categoryRequest.children()) {
                saveCategory(category, childRequest);
            }
        }

        return category;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long categoryId) {
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(()->new CategoryDoesNotExistException(categoryId + ":카테고리 아이디가 존재하지 않습니다."));

        categoryRepository.delete(category);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(CategoryUpdateRequest categoryUpdateRequest) {
        Category category = categoryRepository
                .findCategoryByName(categoryUpdateRequest.oldCategoryName())
                .orElseThrow(()->new CategoryDoesNotExistException(categoryUpdateRequest.oldCategoryName() + ":카테고리 이름이 존재하지 않습니다."));

        category.setName(categoryUpdateRequest.newCategoryName());

        categoryRepository.save(category);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Category read(Long categoryId) {
        return categoryRepository
                .findById(categoryId)
                .orElseThrow(()->new CategoryDoesNotExistException(categoryId + ":카테고리 아이디가 존재하지 않습니다."));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CategoryReadAllResponse readAll() {
        List<Category> allCategories = categoryRepository.findCategoriesByParentIsNull();

        List<CategoryResponse> rootCategories = allCategories.stream()
                .map(this::mapToCategoryResponse)
                .toList();

        return CategoryReadAllResponse.builder()
                .categories(rootCategories)
                .build();
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        List<CategoryResponse> children = category.getChildren().stream()
                .map(this::mapToCategoryResponse)
                .toList();

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .children(children)
                .build();
    }
}
