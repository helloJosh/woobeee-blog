package com.woobeee.blog.post.service.impl;

import com.woobeee.blog.post.dto.CategoryRequest;
import com.woobeee.blog.post.dto.PostCreateRequest;
import com.woobeee.blog.post.dto.PostUpdateRequest;
import com.woobeee.blog.post.entity.Category;
import com.woobeee.blog.post.entity.Post;
import com.woobeee.blog.post.entity.PostTag;
import com.woobeee.blog.post.entity.Tag;
import com.woobeee.blog.post.exception.CategoryDoesNotExistException;
import com.woobeee.blog.post.exception.TagDoesNotExistException;
import com.woobeee.blog.post.repository.*;
import com.woobeee.blog.post.service.CategoryService;
import com.woobeee.blog.post.service.PostService;
import com.woobeee.blog.post.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 게시글 서비스 구현체.
 *
 * @author 김병우
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;

    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final PostCategoryRepository postCategoryRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(PostCreateRequest postCreateRequest) {
        List<String> tags = postCreateRequest.tags();
        Category category = categoryRepository
                .findCategoryByName(postCreateRequest.category().name())
                .orElseThrow(()->new CategoryDoesNotExistException(postCreateRequest.category().name() + ": 카테고리 이름이 존재하지 않습니다."));

        Post post = new Post(postCreateRequest.title(), postCreateRequest.context(), 0L);

        post.addPostCategory(category);

        for (String tag : tags) {
            if (!tagRepository.existsTagByName(tag)) {
                Tag newTag = new Tag(tag);
                tagRepository.save(newTag);
                post.addPostTag(newTag);
            } else {
                Tag newTag = tagRepository.findTagByName(tag)
                        .orElseThrow(()-> new TagDoesNotExistException(tag + ": 태그 이름이 존재하지 않습니다."));
                post.addPostTag(newTag);
            }
        }

        postRepository.save(post);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long delete(Long postId) {
        return 0L;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long update(PostUpdateRequest postUpdateRequest) {
        return 0L;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long read(Long postId) {
        return 0L;
    }
}
