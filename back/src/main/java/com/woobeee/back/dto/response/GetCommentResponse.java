package com.woobeee.back.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GetCommentResponse {
    private Long id;
    private String author;
    private Boolean isEditable;
    private String content;
    private LocalDateTime createdAt;

    private List<GetCommentResponse> replies;
}
