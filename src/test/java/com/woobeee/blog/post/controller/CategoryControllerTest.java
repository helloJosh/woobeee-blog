package com.woobeee.blog.post.controller;

import com.woobeee.blog.BaseDocumentTest;
import com.woobeee.blog.auth.service.AuthenticationService;
import com.woobeee.blog.post.dto.request.CategoryCreateRequest;
import com.woobeee.blog.post.dto.request.CategoryRequest;
import com.woobeee.blog.post.dto.request.CategoryUpdateRequest;
import com.woobeee.blog.post.dto.response.CategoryReadAllResponse;
import com.woobeee.blog.post.dto.response.CategoryResponse;
import com.woobeee.blog.post.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest extends BaseDocumentTest {
    @MockBean
    private CategoryService categoryService;

    @MockBean
    private AuthenticationService authenticationService;

    private CategoryCreateRequest request;
    private CategoryReadAllResponse response;
    @BeforeEach
    void init() {
        CategoryRequest child1 = CategoryRequest.builder()
                .id(1L)
                .name("SPRING-MVC")
                .children(List.of())
                .build();

        CategoryRequest child2 = CategoryRequest.builder()
                .id(2L)
                .name("SPRING-CORE")
                .children(List.of())
                .build();

        CategoryRequest child3 = CategoryRequest.builder()
                .id(3L)
                .name("MYSQL")
                .children(List.of())
                .build();

        CategoryRequest child4 = CategoryRequest.builder()
                .id(4L)
                .name("JPA")
                .children(List.of())
                .build();

        CategoryRequest parent1 = CategoryRequest.builder()
                .id(5L)
                .name("SPRING")
                .children(List.of(child1, child2))
                .build();
        CategoryRequest parent2 = CategoryRequest.builder()
                .id(6L)
                .name("DATABASE")
                .children(List.of(child3, child4))
                .build();
        request = CategoryCreateRequest.builder()
                .categories(List.of(parent1, parent2))
                .build();


        CategoryResponse responseChild1 = CategoryResponse.builder()
                .id(1L)
                .name("SPRING-MVC")
                .children(List.of())
                .build();

        CategoryResponse responseChild2 = CategoryResponse.builder()
                .id(2L)
                .name("SPRING-CORE")
                .children(List.of())
                .build();

        CategoryResponse responseChild3 = CategoryResponse.builder()
                .id(3L)
                .name("MYSQL")
                .children(List.of())
                .build();

        CategoryResponse responseChild4 = CategoryResponse.builder()
                .id(4L)
                .name("JPA")
                .children(List.of())
                .build();

        CategoryResponse responseParent1 = CategoryResponse.builder()
                .id(5L)
                .name("SPRING")
                .children(List.of(responseChild1, responseChild2))
                .build();
        CategoryResponse responseParent2 = CategoryResponse.builder()
                .id(6L)
                .name("DATABASE")
                .children(List.of(responseChild3, responseChild4))
                .build();
        response = CategoryReadAllResponse.builder()
                .categories(List.of(responseParent1, responseParent2))
                .build();
    }


    @DisplayName("카테고리 등록")
    @Test
    void createCategory() throws Exception {

        doNothing().when(categoryService).create(request);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/blog/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "카테고리 등록하는 API",
                        requestFields(
                                fieldWithPath("categories").type(JsonFieldType.ARRAY).description("카테고리 목록"),
                                fieldWithPath("categories[].id").type(JsonFieldType.NUMBER).description("상위 카테고리 아이디"),
                                fieldWithPath("categories[].name").type(JsonFieldType.STRING).description("상위 카테고리 이름"),
                                fieldWithPath("categories[].children").type(JsonFieldType.ARRAY).description("자식 카테고리"),
                                fieldWithPath("categories[].children[].id").type(JsonFieldType.NUMBER).description("자식 카테고리 아이디"),
                                fieldWithPath("categories[].children[].name").type(JsonFieldType.STRING).description("자식 카테고리 이름"),
                                fieldWithPath("categories[].children[].children").type(JsonFieldType.ARRAY).description("더 하위 자식 카테고리 (없을 수 있음)")

                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        )
                ));
    }

    @DisplayName("카테고리 목록 전체 조회")
    @Test
    void readCategories() throws Exception {
        when(categoryService.readAll()).thenReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/blog/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "카테고리 조회하는 API",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data.categories").type(JsonFieldType.ARRAY).description("카테고리 목록"),
                                fieldWithPath("body.data.categories[].id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("body.data.categories[].name").type(JsonFieldType.STRING).description("카테고리 이름"),
                                fieldWithPath("body.data.categories[].children").type(JsonFieldType.ARRAY).description("자식 카테고리"),
                                fieldWithPath("body.data.categories[].children[].id").type(JsonFieldType.NUMBER).description("자식 카테고리 아이디"),
                                fieldWithPath("body.data.categories[].children[].name").type(JsonFieldType.STRING).description("자식 카테고리 이름"),
                                fieldWithPath("body.data.categories[].children[].children").type(JsonFieldType.ARRAY).description("더 하위 자식 카테고리 (없을 수 있음)")
                        )
                ));
    }

    @DisplayName("특정 카테고리 이름 수정")
    @Test
    void updateCategory() throws Exception{
        Long categoryId = 1L;
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .oldCategoryName("OLD_NAME")
                .newCategoryName("NEW_NAME")
                .build();

        // Mock the service to do nothing
        doNothing().when(categoryService).update(request);

        // Perform PUT request and document it
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/blog/categories/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "카테고리 수정 API",
                        requestFields(
                                fieldWithPath("oldCategoryName").type(JsonFieldType.STRING).description("기존 카테고리 이름"),
                                fieldWithPath("newCategoryName").type(JsonFieldType.STRING).description("새로운 카테고리 이름")
                        )
                ));
    }

    @DisplayName("카테고리 삭제")
    @Test
    void deleteCategory() throws Exception {
        Long categoryId = 1L;

        // Mock the service to do nothing
        doNothing().when(categoryService).delete(categoryId);

        // Perform DELETE request and document it
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/blog/categories/{categoryId}", categoryId))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "카테고리 삭제 API"
                ));
    }
}