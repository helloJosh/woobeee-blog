package com.woobeee.blog.post.service.impl;

import com.woobeee.blog.post.dto.CategoryRequest;
import com.woobeee.blog.post.dto.PostCreateRequest;
import com.woobeee.blog.post.dto.PostUpdateRequest;
import com.woobeee.blog.post.entity.*;
import com.woobeee.blog.post.entity.enums.Status;
import com.woobeee.blog.post.exception.*;
import com.woobeee.blog.post.repository.*;
import com.woobeee.blog.post.service.CategoryService;
import com.woobeee.blog.post.service.PostService;
import com.woobeee.blog.post.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        List<Category> categories = new ArrayList<>();

        for (CategoryRequest categoryRequest : postCreateRequest.categories()) {
            Category category = categoryRepository
                    .findCategoryByName(categoryRequest.name())
                    .orElseThrow(()->new CategoryDoesNotExistException(categoryRequest.name() + ": 카테고리 이름이 존재하지 않습니다."));
            categories.add(category);
        }

        Post post = new Post(postCreateRequest.title(), postCreateRequest.context(), 0L);

        for (Category category : categories) {
            post.addPostCategory(category);
        }

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
    public void delete(Long postId) {
        Post post = postRepository
                .findById(postId)
                .orElseThrow(()-> new PostDoesNotExistException(postId + ": 게시글 아이디가 존재하지 않습니다."));

        post.setStatus(Status.NONACTIVE);
        post.setDeletedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(PostUpdateRequest postUpdateRequest) {
        Post post = postRepository
                .findById(postUpdateRequest.postId())
                .orElseThrow(()-> new PostDoesNotExistException(postUpdateRequest.postId() + ": 게시글 아이디가 존재하지 않습니다."));

        List<Category> oldCategories = new ArrayList<>();

        for (PostCategory postCategory : post.getPostCategories() ) {
            oldCategories.add(postCategory.getCategory());
        }

        List<Category> newCategories = new ArrayList<>();

        for (CategoryRequest categoryRequest : postUpdateRequest.categoryRequest()) {
            Category category = categoryRepository
                    .findCategoryByName(categoryRequest.name())
                    .orElseThrow(()->new CategoryDoesNotExistException(categoryRequest.name() + ": 카테고리 이름이 존재하지 않습니다."));
            newCategories.add(category);
        }

        for (Category category : newCategories) {
            if (!oldCategories.contains(category)) {
                post.addPostCategory(category);
            }
        }

        for (Category category : oldCategories) {
            if (!newCategories.contains(category)) {
                PostCategory postCategory = postCategoryRepository
                        .findPostCategoryByCategoryAndPost(category, post)
                        .orElseThrow(() -> new PostCategoryDoesNotExistException("게시글 카테고리가 존재하지 않습니다."));

                postCategoryRepository.delete(postCategory);
            }
        }

        List<Tag> oldTags = new ArrayList<>();
        for (PostTag postTag : post.getPostTags()) {
            oldTags.add(postTag.getTag());
        }

        List<Tag> newTags = new ArrayList<>();

        for (String tagName : postUpdateRequest.tags()) {
            newTags.add(
                    tagRepository.findTagByName(tagName)
                    .orElseThrow(()-> new TagDoesNotExistException(tagName + ": 태그 이름이 존재하지 않습니다."))
            );
        }

        for (Tag tag : oldTags) {
            if(!oldTags.contains(tag)) {
                post.addPostTag(tag);
            }
        }

        for (Tag tag : newTags) {
            if(!newTags.contains(tag)) {
                PostTag postTag = postTagRepository.findPostTagByPostAndTag(post, tag)
                        .orElseThrow(()-> new PostTagDoesNotExistException("게시글 태그가 존재하지 않습니다."));

                postTagRepository.delete(postTag);
            }
        }

        post.setTitle(postUpdateRequest.title());
        post.setContext(postUpdateRequest.context());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Post read(Long postId) {
        Post post = postRepository
                .findById(postId)
                .orElseThrow(()-> new PostDoesNotExistException(postId + ": 게시글 아이디가 존재하지 않습니다."));

        post.setCount(post.getCount() + 1);

        return post;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Post> readCategoryPosts(Long categoryId) {
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(()->new CategoryDoesNotExistException(categoryId + ":카테고리 아이디가 존재하지 않습니다."));

        List<PostCategory> postCategories = postCategoryRepository.findAllByCategory(category);

        return postRepository.findAllByPostCategories(postCategories);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Post> readAll() {
        return postRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Post> readTagPosts(String tagName) {
        Tag tag = tagRepository.findTagByName(tagName)
                .orElseThrow(()-> new TagDoesNotExistException(tagName + ": 태그 이름이 존재하지 않습니다."));

        List<PostTag> postTags = postTagRepository.findPostTagsByTag(tag);

        return postRepository.findAllByPostTags(postTags);
    }
}
