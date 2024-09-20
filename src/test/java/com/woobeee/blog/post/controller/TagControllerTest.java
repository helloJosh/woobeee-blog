package com.woobeee.blog.post.controller;

import com.woobeee.blog.BaseDocumentTest;
import com.woobeee.blog.post.entity.Tag;
import com.woobeee.blog.post.service.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
class TagControllerTest extends BaseDocumentTest {

    @MockBean
    private TagService tagService;

    @DisplayName("태그 목록 조회")
    @Test
    void readTags() throws Exception {
        // Arrange
        List<Tag> tags = List.of(new Tag("Java"), new Tag("Spring"), new Tag("JPA"));
        when(tagService.readAll()).thenReturn(tags);

        // Act & Assert
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/blog/tags")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "태그 조회 API",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.ARRAY).description("태그 목록"),
                                fieldWithPath("body.data[]").type(JsonFieldType.ARRAY).description("태그 이름")
                        )
                ));
    }

    @DisplayName("태그 삭제")
    @Test
    void deleteTag() throws Exception {
        String tagName = "Java";

        doNothing().when(tagService).delete(tagName);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/blog/tags/{tagName}", tagName))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "태그 삭제 API",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        )
                ));
    }
}