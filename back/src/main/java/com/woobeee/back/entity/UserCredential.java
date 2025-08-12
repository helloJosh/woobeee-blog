//package com.woobeee.back.entity;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.Id;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//import org.hibernate.annotations.UuidGenerator;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Builder
//public class UserCredential {
//    /**
//     * This UUID id must be same with UserInfo Tables Id
//     * Later the database will be seperated
//     */
//    @Id
//    @GeneratedValue
//    @UuidGenerator
//    private UUID id;
//
//    private String email;
//
//    /**
//     * This parameter could be
//     * Google Oauth member unique member key
//     * or custom members password
//     */
//    private String password;
//
//    @CreationTimestamp
//    @Builder.Default
//    private LocalDateTime createdAt = LocalDateTime.now();
//
//    @UpdateTimestamp
//    private LocalDateTime updatedAt;
//}
