package com.woobeee.blog.post.entity;

import com.woobeee.blog.member.entity.Like;
import com.woobeee.blog.post.entity.enums.Status;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String title;
    @Setter
    private String context;
    @Setter
    private Status status;
    @Setter
    private Long count;

    private LocalDateTime createdAt;
    @Setter
    private LocalDateTime updatedAt;
    @Setter
    private LocalDateTime deletedAt;

    @PostConstruct
    private void postConstruct() {
        createdAt = LocalDateTime.now();
        status = Status.ACTIVE;
        count = 0L;
    }

    @Builder.Default
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<PostTag> postTags = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<PostCategory> postCategories = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    public Post(String title, String context, Long count) {
        this.title = title;
        this.context = context;
        this.count = count;
        this.createdAt = LocalDateTime.now();
        this.status = Status.ACTIVE;

        this.postTags = new ArrayList<>();
        this.postCategories = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.likes = new ArrayList<>();
        this.images = new ArrayList<>();
    }

    /**
     * 포스트태그 저장 메소드.
     *
     * @param tag 태그
     */
    public void addPostTag(Tag tag){
        PostTag postTag = new PostTag(this, tag);
        this.postTags.add(postTag);
    }

    /**
     * 포스트태그 저장 메소드.
     *
     * @param category 태그
     */
    public void addPostCategory(Category category){
        PostCategory postCategory = new PostCategory(this, category);
        this.postCategories.add(postCategory);
    }

}
