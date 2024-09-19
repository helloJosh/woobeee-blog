package com.woobeee.blog.post.controller;

import com.woobeee.blog.BaseDocumentTest;
import com.woobeee.blog.post.dto.request.CategoryCreateRequest;
import com.woobeee.blog.post.dto.request.CategoryRequest;
import com.woobeee.blog.post.dto.request.PostCreateRequest;
import com.woobeee.blog.post.dto.request.PostUpdateRequest;
import com.woobeee.blog.post.dto.response.CategoryReadAllResponse;
import com.woobeee.blog.post.dto.response.PostReadResponse;
import com.woobeee.blog.post.entity.Post;
import com.woobeee.blog.post.entity.enums.Status;
import com.woobeee.blog.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest extends BaseDocumentTest {

    @MockBean
    private PostService postService;

    private PostCreateRequest postCreateRequest;
    private PostReadResponse postReadResponse;
    private Post post;
    private CategoryCreateRequest request;
    private CategoryReadAllResponse response;
    private List<String> tags;

    @BeforeEach
    void setUp() {
        postCreateRequest = PostCreateRequest.builder()
                .title("Sample Post")
                .context("This is a sample post.")
                .categories(new ArrayList<>())
                .tags(new ArrayList<>())
                .build();

        post = Post.builder()
                .title("Sample Post")
                .context("This is a sample post.")
                .status(Status.ACTIVE)
                .count(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();

        postReadResponse = PostReadResponse.builder()
                .title(post.getTitle())
                .context(post.getContext())
                .status(post.getStatus())
                .count(post.getCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .deletedAt(post.getDeletedAt())
                .build();

        tags = List.of("Java", "Spring", "JPA");
    }

    @DisplayName("게시글 생성")
    @Test
    void savePost() throws Exception {
        doNothing().when(postService).create(postCreateRequest);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/blog/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCreateRequest)))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "게시글 생성 API",
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("context").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("categories").type(JsonFieldType.ARRAY).description("게시글 카테고리"),
                                fieldWithPath("tags").type(JsonFieldType.ARRAY).description("게시글 태그")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        )
                ));
    }

    @DisplayName("게시글 조회")
    @Test
    void readPost() throws Exception {
        Long postId = 1L;
        when(postService.read(postId)).thenReturn(post);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/blog/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "게시글 조회 API",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data.title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("body.data.context").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("body.data.status").type(JsonFieldType.STRING).description("게시글 상태"),
                                fieldWithPath("body.data.count").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("body.data.createdAt").type(JsonFieldType.STRING).description("생성일"),
                                fieldWithPath("body.data.updatedAt").type(JsonFieldType.STRING).description("수정일"),
                                fieldWithPath("body.data.deletedAt").type(JsonFieldType.STRING).optional().description("삭제일")
                        )
                ));
    }

    @DisplayName("게시글 수정")
    @Test
    void updatePost() throws Exception {
        Long postId = 1L;
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .postId(postId)
                .title("Updated Title")
                .context("Updated context.")
                .categories(
                        List.of(CategoryRequest.builder().id(1L).name("SPRING").children(null).build())
                )
                .tags(List.of("Java", "Spring"))
                .build();

        doNothing().when(postService).update(postUpdateRequest);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/blog/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postUpdateRequest)))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "게시글 수정 API",
                        requestFields(
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 아이디"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("context").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("categories").type(JsonFieldType.ARRAY).description("게시글 카테고리"),
                                fieldWithPath("categories[].id").type(JsonFieldType.NUMBER).description("게시글 카테고리 아이디"),
                                fieldWithPath("categories[].name").type(JsonFieldType.STRING).description("게시글 카테고리 이름"),
                                fieldWithPath("categories[].children").type(JsonFieldType.NULL).description("게시글 자식 카테고리"),
                                fieldWithPath("tags").type(JsonFieldType.ARRAY).description("게시글 태그")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        )
                ));
    }

    @DisplayName("게시글 삭제")
    @Test
    void deletePost() throws Exception {
        Long postId = 1L;

        doNothing().when(postService).delete(postId);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/blog/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "게시글 삭제 API",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        )
                ));
    }

    @DisplayName("카테고리별 게시글 조회")
    @Test
    void readCategoryPost() throws Exception {
        Long categoryId = 1L;
        when(postService.readCategoryPosts(categoryId)).thenReturn(List.of(post));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/blog/categories/{categoryId}/posts", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "카테고리별 게시글 조회 API",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.ARRAY).description("게시글 목록"),
                                fieldWithPath("body.data[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("body.data[].context").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("body.data[].status").type(JsonFieldType.STRING).description("게시글 상태"),
                                fieldWithPath("body.data[].count").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("body.data[].createdAt").type(JsonFieldType.STRING).description("생성일"),
                                fieldWithPath("body.data[].updatedAt").type(JsonFieldType.STRING).description("수정일"),
                                fieldWithPath("body.data[].deletedAt").type(JsonFieldType.STRING).optional().description("삭제일")
                        )
                ));
    }

    @DisplayName("전체 게시글 조회")
    @Test
    void readAllPost() throws Exception {
        when(postService.readAll()).thenReturn(List.of(post));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/blog/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "전체 게시글 조회 API",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.ARRAY).description("게시글 목록"),
                                fieldWithPath("body.data[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("body.data[].context").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("body.data[].status").type(JsonFieldType.STRING).description("게시글 상태"),
                                fieldWithPath("body.data[].count").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("body.data[].createdAt").type(JsonFieldType.STRING).description("생성일"),
                                fieldWithPath("body.data[].updatedAt").type(JsonFieldType.STRING).description("수정일"),
                                fieldWithPath("body.data[].deletedAt").type(JsonFieldType.STRING).optional().description("삭제일")
                        )
                ));
    }

    @DisplayName("태그별 게시글 조회")
    @Test
    void readTagPost() throws Exception {
        String tagName = "Java";
        when(postService.readTagPosts(tagName)).thenReturn(List.of(post));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/blog/tags/{tagName}/posts", tagName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "태그별 게시글 조회 API",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.ARRAY).description("게시글 목록"),
                                fieldWithPath("body.data[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("body.data[].context").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("body.data[].status").type(JsonFieldType.STRING).description("게시글 상태"),
                                fieldWithPath("body.data[].count").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("body.data[].createdAt").type(JsonFieldType.STRING).description("생성일"),
                                fieldWithPath("body.data[].updatedAt").type(JsonFieldType.STRING).description("수정일"),
                                fieldWithPath("body.data[].deletedAt").type(JsonFieldType.STRING).optional().description("삭제일")
                        )
                ));
    }
}