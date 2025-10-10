package com.woobeee.chat.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {
    private final MinioProperties properties;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(properties.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())
                ))
                .region(Region.AWS_GLOBAL)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    @Configuration
    @ConfigurationProperties(prefix = "minio")
    @Data
    public static class MinioProperties {
        private String endpoint;
        private String accessKey;
        private String secretKey;
        private String bucket;
    }

    @Bean(destroyMethod = "close")
    public S3Presigner s3Presigner(MinioProperties minio) {
        return S3Presigner.builder()
                // MinIO 엔드포인트 (예: http://localhost:9000)
                .endpointOverride(URI.create(minio.getEndpoint()))
                // 임의의 리전이어도 OK (MinIO는 리전을 검증하지 않음)
                .region(Region.of(("us-east-1")))
                // 접근키/시크릿
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(minio.getAccessKey(), minio.getSecretKey())
                ))
                // 로컬/HTTP + MinIO는 보통 path-style 필요
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }
}
