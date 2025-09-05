package com.woobeee.back.service;


import com.woobeee.back.dto.request.PostPostRequest;
import com.woobeee.back.dto.response.GetPostResponse;
import com.woobeee.back.dto.response.GetPostsResponse;
import com.woobeee.back.entity.*;
import com.woobeee.back.exception.CustomAuthenticationException;
import com.woobeee.back.exception.CustomNotFoundException;
import com.woobeee.back.exception.ErrorCode;
import com.woobeee.back.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final LikeRepository likeRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public void savePost(PostPostRequest request, String loginId) {
        UserInfo userInfo = userInfoRepository
                .findByLoginId(loginId)
                .orElseThrow(() -> new CustomNotFoundException(ErrorCode.login_userNotFound));

        Post post = new Post(
                request.getTitleKo(),
                request.getTitleEn(),
                request.getContentKo(),
                request.getContentEn(),
                request.getCategoryId(),
                userInfo.getId()
        );

        postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId, String loginId) {
        UserInfo userInfo = userInfoRepository
                .findByLoginId(loginId)
                .orElseThrow(() -> new CustomNotFoundException(ErrorCode.login_userNotFound));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomNotFoundException(ErrorCode.post_notFound));

        if (!post.getUserId().equals(userInfo.getId())) {
            throw new CustomAuthenticationException(ErrorCode.comment_needAuthentication);
        }

        postRepository.delete(post);
    }

    @Override
    public GetPostResponse getPost(Long postId, String locale, String loginId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomNotFoundException(ErrorCode.post_notFound));

        String title = locale.equalsIgnoreCase("en") ? post.getTitleEn() : post.getTitleKo();
        String content = locale.equalsIgnoreCase("en") ? post.getTextEn() : post.getTextKo();


        String categoryName = categoryRepository.findById(post.getCategoryId())
                .map(cat -> locale.equalsIgnoreCase("en") ? cat.getNameEn() : cat.getNameKo())
                .orElse("Unknown");

        Long likeCount = likeRepository.countById_PostId(post.getId());

        Boolean isLiked = false;
        if (loginId != null) {
            UserInfo userInfo = userInfoRepository
                    .findByLoginId(loginId)
                    .orElseThrow(() -> new CustomNotFoundException(ErrorCode.login_userNotFound));

            isLiked = likeRepository
                    .existsById(new Like.LikeId(userInfo.getId(), post.getId()));
        }

        return new GetPostResponse(
                post.getId(),
                title,
                content,
                categoryName,
                post.getCategoryId(),
                post.getViews(),
                likeCount,
                isLiked,
                post.getCreatedAt()
        );
    }

    @Override
    public GetPostsResponse getAllPost(String q, String locale, Long categoryId, Pageable pageable) {
        Page<Post> posts;

        if (q != null && categoryId != null) {
            List<Long> categories = findAllChildIdsIncludingSelf(categoryId);
            if (locale.equalsIgnoreCase("en")) {
                posts = postRepository.findByCategoryIdInAndTitleEnContainingIgnoreCaseOrCategoryIdInAndTextEnContainingIgnoreCaseOrderByCreatedAtDesc(
                        categories, q, categories, q, pageable
                );
            } else {
                posts = postRepository.findByCategoryIdInAndTitleEnContainingIgnoreCaseOrCategoryIdInAndTextEnContainingIgnoreCaseOrderByCreatedAtDesc(
                        categories, q, categories, q, pageable
                );
            }
        } else if (q == null && categoryId != null) {
            List<Long> categories = findAllChildIdsIncludingSelf(categoryId);
            posts = postRepository.findAllByCategoryIdIn(categories, pageable);
        } else if (categoryId == null && q != null) {
            if (locale.equalsIgnoreCase("en")) {
                posts = postRepository.findByTitleEnContainingIgnoreCaseOrTextEnContainingIgnoreCaseOrderByCreatedAtDesc(
                        q, q, pageable
                );
            } else {
                posts = postRepository.findByTitleKoContainingIgnoreCaseOrTextKoContainingIgnoreCaseOrderByCreatedAtDesc(
                        q, q, pageable
                );
            }
        } else {
            posts = postRepository.findAll(pageable);
        }

        List<GetPostsResponse.PostContent> contents = posts.getContent().stream().map(post -> {
            String title = locale.equalsIgnoreCase("en") ? post.getTitleEn() : post.getTitleKo();
            String content = locale.equalsIgnoreCase("en") ? post.getTextEn() : post.getTextKo();
            String categoryName = categoryRepository.findById(post.getCategoryId())
                    .map(cat -> locale.equalsIgnoreCase("en") ? cat.getNameEn() : cat.getNameKo())
                    .orElse("Unknown");

            Long likeCount = likeRepository.countById_PostId(post.getId());

            return new GetPostsResponse.PostContent(
                    post.getId(),
                    title,
                    content,
                    categoryName,
                    post.getCategoryId(),
                    post.getViews(),
                    likeCount,
                    post.getCreatedAt()
            );
        }).toList();

        return new GetPostsResponse(posts.hasNext(), contents);
    }

    public List<Long> findAllChildIdsIncludingSelf(Long parentId) {
        List<Long> ids = new ArrayList<>();
        ids.add(parentId);
        List<Category> children = categoryRepository.findAllByParentId(parentId);
        for (Category child : children) {
            ids.addAll(findAllChildIdsIncludingSelf(child.getId()));
        }
        return ids;
    }
}
