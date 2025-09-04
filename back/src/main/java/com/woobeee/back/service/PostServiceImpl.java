package com.woobeee.back.service;


import com.woobeee.back.dto.request.PostPostRequest;
import com.woobeee.back.dto.response.GetPostsResponse;
import com.woobeee.back.entity.Category;
import com.woobeee.back.entity.Comment;
import com.woobeee.back.entity.Post;
import com.woobeee.back.repository.CategoryRepository;
import com.woobeee.back.repository.CommentRepository;
import com.woobeee.back.repository.LikeRepository;
import com.woobeee.back.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scala.concurrent.impl.FutureConvertersImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final LikeRepository likeRepository;

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
                    String.valueOf(post.getCategoryId()),
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
