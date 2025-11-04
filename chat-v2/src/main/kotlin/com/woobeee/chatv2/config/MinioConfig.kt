package com.woobeee.chatv2.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

@Configuration
class MinioConfig(
    private val properties: MinioProperties
) {

    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .endpointOverride(URI.create(properties.endpoint))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(properties.accessKey, properties.secretKey)
                )
            )
            .region(Region.AWS_GLOBAL)
            .serviceConfiguration(
                S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .build()
            )
            .build()
    }

    @Bean(destroyMethod = "close")
    fun s3Presigner(minio: MinioProperties): S3Presigner {
        return S3Presigner.builder()
            .endpointOverride(URI.create(minio.endpoint))
            .region(Region.of("us-east-1")) // MinIO는 리전 검증 안 함
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(minio.accessKey, minio.secretKey)
                )
            )
            .serviceConfiguration(
                S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .build()
            )
            .build()
    }

    @Configuration
    @ConfigurationProperties(prefix = "minio")
    data class MinioProperties(
        var endpoint: String = "",
        var accessKey: String = "",
        var secretKey: String = "",
        var bucket: String = ""
    )
}