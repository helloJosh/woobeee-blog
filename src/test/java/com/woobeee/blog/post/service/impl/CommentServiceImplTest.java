package com.woobeee.blog.post.service.impl;

import com.woobeee.blog.member.entity.Member;
import com.woobeee.blog.member.exception.MemberDoesNotExistException;
import com.woobeee.blog.member.repository.MemberRepository;
import com.woobeee.blog.post.dto.request.CommentCreateRequest;
import com.woobeee.blog.post.dto.request.CommentUpdateRequest;
import com.woobeee.blog.post.dto.response.CommentReadAllResponse;
import com.woobeee.blog.post.entity.Comment;
import com.woobeee.blog.post.entity.Post;
import com.woobeee.blog.post.exception.CommentDoesNotExistException;
import com.woobeee.blog.post.exception.PostDoesNotExistException;
import com.woobeee.blog.post.repository.CommentRepository;
import com.woobeee.blog.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @DisplayName("게시글이 없을 때 테스트 실패")
    @Test
    void create_shouldThrowExceptionWhenPostDoesNotExist() {
        CommentCreateRequest request = new CommentCreateRequest(1L, 1L, 1L, "comment");
        when(postRepository.findById(request.postId())).thenReturn(Optional.empty());

        assertThrows(PostDoesNotExistException.class, () -> commentService.create(request));

        verify(postRepository, times(1)).findById(request.postId());
    }

    @DisplayName("맴버가 아닐때 테스트 실패")
    @Test
    void create_shouldThrowExceptionWhenMemberDoesNotExist() {
        Post post = new Post("Title", "Context", 0L);
        CommentCreateRequest request = new CommentCreateRequest(1L, 1L, 1L, "comment");
        when(postRepository.findById(request.postId())).thenReturn(Optional.of(post));
        when(memberRepository.findById(request.memberId())).thenReturn(Optional.empty());

        assertThrows(MemberDoesNotExistException.class, () -> commentService.create(request));

        verify(postRepository, times(1)).findById(request.postId());
        verify(memberRepository, times(1)).findById(request.memberId());
    }

    @DisplayName("새 댓글 테스트 성공")
    @Test
    void create_shouldSaveNewCommentSuccessfully() {
        CommentCreateRequest request = new CommentCreateRequest(null, 1L, 1L, "comment");
        Post post = new Post("Title", "Context", 0L);
        Member member = Member.builder().id(1L).build();
        when(postRepository.findById(request.postId())).thenReturn(Optional.of(post));
        when(memberRepository.findById(request.memberId())).thenReturn(Optional.of(member));

        commentService.create(request);

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @DisplayName("댓글 삭제시 댓글 없을때 테스트 실패")
    @Test
    void delete_shouldThrowExceptionWhenCommentDoesNotExist() {
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(CommentDoesNotExistException.class, () -> commentService.delete(commentId));

        verify(commentRepository, times(1)).findById(commentId);
    }

    @DisplayName("댓글 삭제시 댓글 상태를 삭제 테스트 성공")
    @Test
    void delete_shouldSetCommentAsDeleted() {
        Long commentId = 1L;
        Post post = new Post("Title", "Context", 0L);
        Member member = Member.builder().id(1L).build();
        Comment comment = new Comment("comment", null, member, post);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.delete(commentId);

        verify(commentRepository, times(1)).save(comment);
        verify(commentRepository, times(1)).findById(commentId);
    }

    @DisplayName("댓글 수정시 댓글 없을 때 오류 테스트")
    @Test
    void update_shouldThrowExceptionWhenCommentDoesNotExist() {
        CommentUpdateRequest request = new CommentUpdateRequest(1L, "이전 댓글", "새 댓글");
        when(commentRepository.findById(request.commentId())).thenReturn(Optional.empty());

        assertThrows(CommentDoesNotExistException.class, () -> commentService.update(request));

        verify(commentRepository, times(1)).findById(request.commentId());
    }

    @DisplayName("수정 성공 테스트")
    @Test
    void update_shouldUpdateCommentSuccessfully() {
        CommentUpdateRequest request = new CommentUpdateRequest(1L, "이전 댓글", "새 댓글");
        Post post = new Post("Title", "Context", 0L);
        Member member = Member.builder().id(1L).build();
        Comment comment = new Comment("comment", null, member, post);
        when(commentRepository.findById(request.commentId())).thenReturn(Optional.of(comment));

        commentService.update(request);

        verify(commentRepository, times(1)).save(comment);
        verify(commentRepository, times(1)).findById(request.commentId());
    }

    @DisplayName("전체 댓글 조회 테스트")
    @Test
    void readAll_shouldReturnCommentReadAllResponse() {
        Long postId = 1L;
        Post post = new Post("Title", "Context", 0L);
        Member member = Member.builder().id(1L).build();
        Comment comment = new Comment("comment", null, member, post);
        when(commentRepository.findByPostIdAndParentIsNull(postId)).thenReturn(List.of(comment));

        CommentReadAllResponse response = commentService.readAll(postId);

        verify(commentRepository, times(1)).findByPostIdAndParentIsNull(postId);
    }
}