package com.woobeee.blog.post.service.impl;

import com.woobeee.blog.member.entity.Member;
import com.woobeee.blog.member.exception.MemberDoesNotExistException;
import com.woobeee.blog.member.repository.MemberRepository;
import com.woobeee.blog.post.dto.request.CommentCreateRequest;
import com.woobeee.blog.post.dto.request.CommentUpdateRequest;
import com.woobeee.blog.post.dto.response.CommentReadAllResponse;
import com.woobeee.blog.post.dto.response.CommentResponse;
import com.woobeee.blog.post.entity.Comment;
import com.woobeee.blog.post.entity.Post;
import com.woobeee.blog.post.entity.enums.Status;
import com.woobeee.blog.post.exception.CommentDoesNotExistException;
import com.woobeee.blog.post.exception.PostDoesNotExistException;
import com.woobeee.blog.post.repository.CommentRepository;
import com.woobeee.blog.post.repository.PostRepository;
import com.woobeee.blog.post.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 댓글 서비스 구현체.
 *
 * @author 김병우
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;


    /**
     * {@inheritDoc}
     */
    @Override
    public void create(CommentCreateRequest commentCreateRequest) {
        Post post = postRepository
                .findById(commentCreateRequest.postId())
                .orElseThrow(
                        () -> new PostDoesNotExistException(commentCreateRequest.postId() + ": 게시글이 존재하지 않습니다.")
                );
        Member member = memberRepository
                .findById(commentCreateRequest.memberId())
                .orElseThrow(
                        () -> new MemberDoesNotExistException(commentCreateRequest.memberId() + ": 회원이 존재하지 않습니다.")
                );

        Comment comment = null;

        if (commentCreateRequest.parentCommentId() != null) {
            comment = commentRepository.findById(commentCreateRequest.parentCommentId())
                    .orElseThrow(
                            () -> new CommentDoesNotExistException(commentCreateRequest.parentCommentId() + ": 댓글이 존재하지 않습니다.")
                    );
        }


        Comment newComment = new Comment(
                commentCreateRequest.context(),
                comment,
                member,
                post
        );

        commentRepository.save(newComment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(
                        () -> new CommentDoesNotExistException(commentId + ": 댓글이 존재하지 않습니다.")
                );

        comment.setDeletedAt(LocalDateTime.now());
        comment.setStatus(Status.NONACTIVE);

        commentRepository.save(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(CommentUpdateRequest commentUpdateRequest) {
        Comment comment = commentRepository.findById(commentUpdateRequest.commentId())
                .orElseThrow(
                        () -> new CommentDoesNotExistException(commentUpdateRequest.commentId() + ": 댓글이 존재하지 않습니다.")
                );

        if (!commentUpdateRequest.newContext().equals(commentUpdateRequest.oldContext())) {
            comment.setContext(commentUpdateRequest.newContext());
            comment.setUpdatedAt(LocalDateTime.now());
        }

        commentRepository.save(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentReadAllResponse readAll(Long postId) {
        List<Comment> allComments = commentRepository.findByPostIdAndParentIsNull(postId);

        List<CommentResponse> rootComments = allComments.stream()
                .map(this::mapToCommentResponse)
                .toList();

        return CommentReadAllResponse.builder()
                .commentResponses(rootComments)
                .build();
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        List<CommentResponse> children = comment.getChildren().stream()
                .map(this::mapToCommentResponse)
                .toList();

        return CommentResponse.builder()
                .id(comment.getId())
                .context(comment.getContext())
                .children(children)
                .build();
    }
}
