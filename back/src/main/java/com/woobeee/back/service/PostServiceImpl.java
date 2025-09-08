package com.woobeee.back.service;


import com.woobeee.back.config.MinioConfig;
import com.woobeee.back.dto.request.PostPostRequest;
import com.woobeee.back.dto.response.GetPostResponse;
import com.woobeee.back.dto.response.GetPostsResponse;
import com.woobeee.back.entity.*;
import com.woobeee.back.exception.CustomAuthenticationException;
import com.woobeee.back.exception.CustomInternalServerException;
import com.woobeee.back.exception.CustomNotFoundException;
import com.woobeee.back.exception.ErrorCode;
import com.woobeee.back.repository.*;
import com.woobeee.back.support.ProgressInputStream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final LikeRepository likeRepository;
    private final UserInfoRepository userInfoRepository;

    private final S3Client s3Client;
    private final MinioConfig.MinioProperties minio;
    private final S3Presigner s3Presigner;

    /**
     * 이미지 삽입 글작성시: 마크다운에는 ![설명](${파일명}) 형태로 넣어두세요.
     */
    @SneakyThrows
    @Override
    public void savePost (
            PostPostRequest request,
            String loginId,
            MultipartFile markdownEn,
            MultipartFile markdownKr,
            List<MultipartFile> files
    ) {
        UserInfo userInfo = userInfoRepository
                .findByLoginId(loginId)
                .orElseThrow(() -> new CustomNotFoundException(ErrorCode.login_userNotFound));

        String markdownEnString = (markdownEn != null && !markdownEn.isEmpty())
                ? new String(markdownEn.getBytes(), StandardCharsets.UTF_8)
                : "";

        String markdownKrString = (markdownKr != null && !markdownKr.isEmpty())
                ? new String(markdownKr.getBytes(), StandardCharsets.UTF_8)
                : "";

        Post post = new Post(
                request.getTitleKo(),
                request.getTitleEn(),
                markdownKrString,
                markdownEnString,
                request.getCategoryId(),
                userInfo.getId()
        );

        post = postRepository.save(post);

        if (files != null && !files.isEmpty()) {
            AtomicInteger lastPrintedPercent = new AtomicInteger(-1);

            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) continue;

                String original = file.getOriginalFilename();
                if (original == null) continue;
                String fileName = Paths.get(original).getFileName().toString().trim(); // 경로 제거

                String key = post.getId() + "/" + fileName;

                try (
                        InputStream is = file.getInputStream();
                        ProgressInputStream pis = new ProgressInputStream(
                                is,
                                file.getSize(),
                                percent -> {
                                    int p = (int) percent.doubleValue();
                                    if (p != lastPrintedPercent.getAndSet(p)) {
                                        log.info("Upload progress: {}%", p);
                                    }
                                }
                        )
                ) {
                    s3Client.putObject(
                            PutObjectRequest.builder()
                                    .bucket(minio.getBucket())
                                    .key(key)
                                    .contentType(file.getContentType())
                                    .build(),
                            RequestBody.fromInputStream(pis, file.getSize())
                    );
                } catch (IOException e) {
                    throw new CustomInternalServerException(ErrorCode.post_imageUploadError);
                }

                // ${파일명} -> https://<endpoint>/<bucket>/<postId>/<파일명>
//                String publicUrl = minio.getEndpoint() + "/" + minio.getBucket() + "/" + key;
//
//                if (!markdownEnString.isBlank())
//                    markdownEnString = markdownEnString.replace("${" + fileName + "}", publicUrl);
//
//                if (!markdownKrString.isBlank())
//                    markdownKrString = markdownKrString.replace("${" + fileName + "}", publicUrl);
            }
        }

//        if (!markdownEnString.isBlank())
//            post.setTextEn(markdownEnString);
//        if (!markdownKrString.isBlank())
//            post.setTextKo(markdownKrString);
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

        content = replaceImagePlaceholdersWithPresignedUrls(content, postId);

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
    private String replaceImagePlaceholdersWithPresignedUrls(String markdown, Long postId) {
        if (markdown == null || markdown.isBlank()) return markdown;

        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(markdown);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String fileName = matcher.group(1); // ${fileName} 에서 fileName 추출

            // Presigned URL 생성
            String presignedUrl = generatePresignedUrl(postId, fileName);

            matcher.appendReplacement(result, Matcher.quoteReplacement(presignedUrl));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String generatePresignedUrl(Long postId, String fileName) {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(minio.getBucket())
                .key(postId + "/" + fileName)
                .build();

        GetObjectPresignRequest preReq = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofDays(7))
                .getObjectRequest(getReq)
                .build();

        return s3Presigner.presignGetObject(preReq).url().toString();
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
