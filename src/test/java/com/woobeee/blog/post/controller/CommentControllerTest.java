package com.woobeee.blog.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woobeee.blog.BaseDocumentTest;
import com.woobeee.blog.api.Response;
import com.woobeee.blog.post.dto.request.CommentCreateRequest;
import com.woobeee.blog.post.dto.request.CommentUpdateRequest;
import com.woobeee.blog.post.dto.response.CategoryReadAllResponse;
import com.woobeee.blog.post.dto.response.CommentReadAllResponse;
import com.woobeee.blog.post.dto.response.CommentResponse;
import com.woobeee.blog.post.entity.Comment;
import com.woobeee.blog.post.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CommentController.class)
class CommentControllerTest extends BaseDocumentTest {
    @MockBean
    private CommentService commentService;

    @DisplayName("댓글 생성 테스트")
    @Test
    void saveComment() throws Exception {
        CommentCreateRequest commentCreateRequest = CommentCreateRequest.builder()
                .parentCommentId(null)
                .postId(1L)
                .memberId(1L)
                .context("").build();

        doNothing().when(commentService).create(commentCreateRequest);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/blog/posts/{postId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateRequest)))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "댓글 등록 API",
                        requestFields(
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("context").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("parentCommentId").type(JsonFieldType.NUMBER).optional().description("부모 댓글 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        )
                ));
    }

    @DisplayName("댓글 조회 테스트")
    @Test
    void readAllComments() throws Exception {
        CommentResponse commentResponse = CommentResponse.builder()
                .id(1L)
                .context("test comment")
                .children(null)
                .build();

        CommentReadAllResponse response = CommentReadAllResponse
                .builder()
                .postId(1L)
                .commentResponses(List.of(commentResponse))
                .build();

        when(commentService.readAll(1L)).thenReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/blog/posts/{postId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "댓글 전체 조회 API",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data.postId").type(JsonFieldType.NUMBER).description("게시글 아이디"),
                                fieldWithPath("body.data.commentResponses").type(JsonFieldType.ARRAY).description("댓글 목록"),
                                fieldWithPath("body.data.commentResponses[].id").type(JsonFieldType.NUMBER).description("댓글 목록"),
                                fieldWithPath("body.data.commentResponses[].context").type(JsonFieldType.STRING).description("댓글 목록"),
                                fieldWithPath("body.data.commentResponses[].children").type(JsonFieldType.NULL).description("댓글 목록")

                        )
                ));
    }

    @DisplayName("댓글 수정 테스트")
    @Test
    void updateComment() throws Exception {
        CommentUpdateRequest commentUpdateRequest = CommentUpdateRequest.builder()
                .commentId(1L)
                .newContext("newComment")
                .oldContext("oldComment").build();

        doNothing().when(commentService).update(commentUpdateRequest);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/blog/posts/{postId}/comments/{commentId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentUpdateRequest)))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "댓글 수정 API",
                        requestFields(
                                fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                fieldWithPath("oldContext").type(JsonFieldType.STRING).description("이전 댓글 내용"),
                                fieldWithPath("newContext").type(JsonFieldType.STRING).description("새로운 댓글 내용")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        )
                ));
    }

    @DisplayName("댓글 삭제 테스트")
    @Test
    void deleteComment() throws Exception {
        doNothing().when(commentService).delete(1L);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/blog/posts/{postId}/comments/{commentId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "댓글 삭제 API",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        )
                ));
    }
}