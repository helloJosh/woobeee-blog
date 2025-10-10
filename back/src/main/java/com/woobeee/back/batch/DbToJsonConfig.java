package com.woobeee.back.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woobeee.back.config.MinioConfig;
import com.woobeee.back.dto.batch.PostExportDto;
import com.woobeee.back.entity.Post;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class DbToJsonConfig {
    private final EntityManagerFactory emf;
    private final S3Client s3Client;
    private final MinioConfig.MinioProperties minioProperties;

    @Bean
    public Step exportPostsOnceStep(JobRepository jobRepository,
                                    PlatformTransactionManager txManager,
                                    ObjectMapper om) {
        return new StepBuilder("exportPostsOnceStep", jobRepository)
                .tasklet((contribution, context) -> {
                    var em = emf.createEntityManager();
                    try {
                        var list = em.createQuery("""
                        SELECT p, c.nameKo, c.nameEn
                        FROM Post p
                        LEFT JOIN Category c ON p.categoryId = c.id
                        ORDER BY p.id
                    """, Object[].class).getResultList();

                        List<PostExportDto> result = list.stream().map(arr -> {
                            Post p = (Post) arr[0];
                            return new PostExportDto(
                                    p.getId(),
                                    "https://woobeee.com/post/" + p.getId(),
                                    Optional.ofNullable(p.getTitleKo()).orElse(""),
                                    Optional.ofNullable(p.getTitleEn()).orElse(""),
                                    Optional.ofNullable(p.getTextKo()).orElse(""),
                                    Optional.ofNullable(p.getTextEn()).orElse(""),
                                    Optional.ofNullable(p.getViews()).orElse(0L),
                                    p.getCreatedAt(),
                                    p.getUpdatedAt(),
                                    (String) arr[1],
                                    (String) arr[2]
                            );
                        }).toList();

                        byte[] json = om.writerWithDefaultPrettyPrinter().writeValueAsBytes(result);
                        s3Client.putObject(
                                PutObjectRequest.builder()
                                        .bucket(minioProperties.getBucket())
                                        .key("post.json")
                                        .contentType("application/json")
                                        .build(),
                                RequestBody.fromBytes(json)
                        );
                    } finally {
                        em.close();
                    }
                    return org.springframework.batch.repeat.RepeatStatus.FINISHED;
                }, txManager)
                .build();
    }


    // Job
    @Bean
    public Job exportPostsJob(JobRepository jobRepository, Step exportPostsOnceStep) {
        return new JobBuilder("exportPostsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(exportPostsOnceStep)
                .build();
    }


//
//    // 1) Reader: 모든 Post 페이징 조회
//    @Bean
//    public JpaPagingItemReader<Object[]> postReader() {
//        return new JpaPagingItemReaderBuilder<Object[]>()
//                .name("postReader")
//                .entityManagerFactory(emf)
//                .queryString("""
//                SELECT p, c.nameKo, c.nameEn
//                FROM Post p
//                LEFT JOIN Category c ON p.categoryId = c.id
//                ORDER BY p.id
//                """)
//                .pageSize(100)
//                .build();
//    }
//
//    // 2) Processor: Post → PostExportDto (URL 생성 포함)
//    @Bean
//    public ItemProcessor<Object[], PostExportDto> postProcessor() {
//        return arr -> {
//            Post p = (Post) arr[0];
//            String categoryNameKo = (String) arr[1];
//            String categoryNameEn = (String) arr[2];
//
//            return new PostExportDto(
//                    p.getId(),
//                    "https://woobeee.com/post/" + p.getId(),
//                    Optional.ofNullable(p.getTitleKo()).orElse(""),
//                    Optional.ofNullable(p.getTitleEn()).orElse(""),
//                    Optional.ofNullable(p.getTextKo()).orElse(""),
//                    Optional.ofNullable(p.getTextEn()).orElse(""),
//                    Optional.ofNullable(p.getViews()).orElse(0L),
//                    p.getCreatedAt(),
//                    p.getUpdatedAt(),
//                    categoryNameKo,
//                    categoryNameEn
//            );
//        };
//    }
//
//    // 3) Writer: JSON 직렬화 → MinIO(S3) 업로드
//    @Bean
//    public ItemWriter<PostExportDto> postWriter(ObjectMapper om) {
//        return (Chunk<? extends PostExportDto> items) -> {
//            List<PostExportDto> result = new ArrayList<>();
//            for (PostExportDto dto : items) {
//                result.add(dto);
//            }
//            byte[] json = om.writerWithDefaultPrettyPrinter().writeValueAsBytes(result);
//            String key = "post.json";
//
//            var put = PutObjectRequest.builder()
//                    .bucket(minioProperties.getBucket())
//                    .key(key)
//                    .contentType("application/json")
//                    .build();
//
//            s3Client.putObject(put, RequestBody.fromBytes(json));
//        };
//    }
    // Step
//    @Bean
//    public Step exportPostsStep(JobRepository jobRepository,
//                                PlatformTransactionManager txManager,
//                                JpaPagingItemReader<Object[]> reader,
//                                ItemProcessor<Object[], PostExportDto> processor,
//                                ItemWriter<PostExportDto> writer) {
//        return new StepBuilder("exportPostsStep", jobRepository)
//                .<Object[], PostExportDto>chunk(100, txManager)
//                .reader(reader)
//                .processor(processor)
//                .writer(writer)
//                .build();
//    }

}
