package com.woobeee.back.service;


import com.woobeee.back.dto.request.PostPostRequest;
import com.woobeee.back.entity.Comment;
import com.woobeee.back.entity.Post;
import com.woobeee.back.repository.CategoryRepository;
import com.woobeee.back.repository.CommentRepository;
import com.woobeee.back.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public void savePost(PostPostRequest request, UUID userId) {
        Post post = new Post(
                request.getTitleKo(),
                request.getTitleEn(),
                request.getContentKo(),
                request.getContentEn(),
                request.getCategoryId(),
                userId
        );

        postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId, UUID userId) {

        // TODO : exception 정리
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("댓글을 삭제할 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    @Override
    public void getAllPost(String q, String locale, Pageable pageable) {

    }

    @Override
    public void getAllPost(String q, String locale, Long categoryId, Pageable pageable) {

    }
}
