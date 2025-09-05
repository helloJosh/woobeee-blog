package com.woobeee.back.service;

import com.woobeee.back.dto.request.PostCommentRequest;
import com.woobeee.back.dto.response.GetCommentResponse;
import com.woobeee.back.entity.Comment;
import com.woobeee.back.entity.UserInfo;
import com.woobeee.back.exception.CustomAuthenticationException;
import com.woobeee.back.exception.CustomNotFoundException;
import com.woobeee.back.exception.ErrorCode;
import com.woobeee.back.repository.CommentRepository;
import com.woobeee.back.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public void saveComment (
            PostCommentRequest request,
            String loginId
    ) {
        if (loginId == null) {
            throw new CustomAuthenticationException(ErrorCode.comment_needAuthentication);
        }

        UserInfo userInfo = userInfoRepository
                .findByLoginId(loginId)
                .orElseThrow(() -> new CustomNotFoundException(ErrorCode.login_userNotFound));

        Comment comment = new Comment (
                request.getContent(),
                request.getPostId(),
                request.getParentId(),
                userInfo.getId()
        );

        commentRepository.save(comment);
    }

    @Override
    public void deleteComment (
            Long commentId,
            String loginId
    ) {
        if (loginId == null) {
            throw new CustomAuthenticationException(ErrorCode.comment_needAuthentication);
        }

        UserInfo userInfo = userInfoRepository
                .findByLoginId(loginId)
                .orElseThrow(() -> new CustomNotFoundException(ErrorCode.login_userNotFound));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

        if (!comment.getUserInfoId().equals(userInfo.getId())) {
            throw new RuntimeException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    @Override
    public List<GetCommentResponse> getAllCommentsFromPost (
            Long postId,
            String loginId
    ) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        Map<Long, GetCommentResponse> map = new HashMap<>();

        List<GetCommentResponse> roots = new ArrayList<>();

        for (Comment comment : comments) {

            UserInfo commentWritterUserInfo = userInfoRepository
                    .findById(comment.getUserInfoId())
                    .orElseThrow(() -> new CustomNotFoundException(ErrorCode.login_userNotFound));

            map.put(comment.getId(), new GetCommentResponse(
                    comment.getId(),
                    commentWritterUserInfo.getLoginId(),
                    commentWritterUserInfo.getLoginId().equals(loginId),
                    comment.getContent(),
                    comment.getCreatedAt(),
                    new ArrayList<>()
            ));
        }

        // 4. 댓글 계층 구성
        for (Comment comment : comments) {
            if (comment.getParentId() == null) {
                // 최상위 댓글
                roots.add(map.get(comment.getId()));
            } else {
                // 자식 댓글 → 부모에 추가
                GetCommentResponse parent = map.get(comment.getParentId());
                if (parent != null) {
                    parent.getReplies().add(map.get(comment.getId()));
                }
            }
        }

        return roots;
    }
}
