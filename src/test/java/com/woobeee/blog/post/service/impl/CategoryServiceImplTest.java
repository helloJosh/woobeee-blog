package com.woobeee.blog.post.service.impl;

import com.woobeee.blog.post.dto.request.CategoryCreateRequest;
import com.woobeee.blog.post.dto.request.CategoryRequest;
import com.woobeee.blog.post.dto.request.CategoryUpdateRequest;
import com.woobeee.blog.post.dto.response.CategoryReadAllResponse;
import com.woobeee.blog.post.entity.Category;
import com.woobeee.blog.post.exception.CategoryDoesNotExistException;
import com.woobeee.blog.post.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @DisplayName("카테고리 생성")
    @Test
    void createCategory() {
        CategoryRequest child1 = CategoryRequest.builder().name("Child1").children(List.of()).build();
        CategoryRequest parent = CategoryRequest.builder().name("Parent").children(List.of(child1)).build();
        CategoryCreateRequest request = CategoryCreateRequest.builder().categories(List.of(parent)).build();

        when(categoryRepository.existsCategoryByName(any())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(any(Category.class));

        categoryService.create(request);

        verify(categoryRepository, times(2)).save(any(Category.class)); // Parent and child
    }

    @DisplayName("카테고리 삭제")
    @Test
    void deleteCategory() {
        Long categoryId = 1L;
        Category category = Category.builder().build();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        categoryService.delete(categoryId);

        verify(categoryRepository, times(1)).delete(category);
    }

    @DisplayName("카테고리 삭제 - 존재하지 않는 경우")
    @Test
    void deleteCategory_NotExist() {
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.delete(categoryId))
                .isInstanceOf(CategoryDoesNotExistException.class)
                .hasMessageContaining(categoryId + ":카테고리 아이디가 존재하지 않습니다.");
    }

    @DisplayName("카테고리 업데이트")
    @Test
    void updateCategory() {
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .oldCategoryName("OldName")
                .newCategoryName("NewName")
                .build();
        Category category = Category.builder().build();
        category.setName("OldName");

        when(categoryRepository.findCategoryByName(request.oldCategoryName()))
                .thenReturn(Optional.of(category));

        categoryService.update(request);

        assertThat(category.getName()).isEqualTo("NewName");
        verify(categoryRepository, times(1)).save(category);
    }

    @DisplayName("카테고리 업데이트 - 존재하지 않는 경우")
    @Test
    void updateCategory_NotExist() {
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .oldCategoryName("OldName")
                .newCategoryName("NewName")
                .build();
        when(categoryRepository.findCategoryByName(request.oldCategoryName()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(request))
                .isInstanceOf(CategoryDoesNotExistException.class)
                .hasMessageContaining(request.oldCategoryName() + ":카테고리 이름이 존재하지 않습니다.");
    }

    @DisplayName("카테고리 전체 조회")
    @Test
    void readAllCategories() {
        Category parent = new Category("Parent");
        Category child = new Category("Child");
        parent.addChildren(child);

        when(categoryRepository.findCategoriesByParentIsNull()).thenReturn(List.of(parent));

        CategoryReadAllResponse response = categoryService.readAll();

        assertThat(response.categories()).hasSize(1);
        assertThat(response.categories().getFirst().name()).isEqualTo("Parent");
        assertThat(response.categories().getFirst().children()).hasSize(1);
        assertThat(response.categories().getFirst().children().getFirst().name()).isEqualTo("Child");
    }


}