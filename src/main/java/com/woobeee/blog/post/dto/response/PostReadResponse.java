package com.woobeee.blog.post.dto.response;

import com.woobeee.blog.post.entity.enums.Status;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 게시글 읽기 응답.
 *
 * @param title 게시글 제목
 * @param context 게시글 내용
 * @param status 게시글 상태
 * @param count 게시글 조회수
 * @param createdAt 생성일자
 * @param updatedAt 수정일자
 * @param deletedAt 삭제일자
 */
@Builder
public record PostReadResponse(
        String title,
        String context,
        Status status,
        Long count,

        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
}
