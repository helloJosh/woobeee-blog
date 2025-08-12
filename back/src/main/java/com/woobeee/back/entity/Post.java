//package com.woobeee.back.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//
//import java.time.LocalDateTime;
//
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Builder
//public class Post {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String title;
//
//    @Column(columnDefinition = "text")
//    private String text;
//    private Long views;
//
//    @CreationTimestamp
//    @Builder.Default
//    private LocalDateTime createdAt = LocalDateTime.now();
//
//    @UpdateTimestamp
//    private LocalDateTime updatedAt;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Category category;
//}
