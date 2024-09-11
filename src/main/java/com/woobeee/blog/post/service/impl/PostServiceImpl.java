package com.woobeee.blog.post.service.impl;

import com.woobeee.blog.post.dto.PostCreateRequest;
import com.woobeee.blog.post.dto.PostUpdateRequest;
import com.woobeee.blog.post.entity.PostTag;
import com.woobeee.blog.post.repository.*;
import com.woobeee.blog.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final PostTagRepository postTagRepository;
    private final PostCategoryRepository postCategoryRepository;

    @Override
    public Long create(PostCreateRequest postCreateRequest) {
        return 0L;
    }

    @Override
    public Long delete(Long postId) {
        return 0L;
    }

    @Override
    public Long update(PostUpdateRequest postUpdateRequest) {
        return 0L;
    }

    @Override
    public Long read(Long postId) {
        return 0L;
    }
}
