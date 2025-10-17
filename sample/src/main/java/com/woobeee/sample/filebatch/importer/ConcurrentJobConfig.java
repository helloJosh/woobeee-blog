package com.woobeee.sample.filebatch.importer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woobeee.sample.filebatch.config.MinioConfig;
import com.woobeee.sample.filebatch.support.FlatFileHeaderExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ConcurrentJobConfig {
    private final int THREAD_SIZE = 1;

    @Bean
    @StepScope
    public ConcurrentFlatFileItemReader concurrentFlatFileItemReader(
            ObjectMapper objectMapper,
            S3Client s3Client,
            MinioConfig.MinioProperties minio,
            FlatFileHeaderExtractor headerExtractor,
            @Value("#{stepExecutionContext['key']}") String key,
            @Value("#{stepExecutionContext['startOffset']}") Long startOffset,
            @Value("#{stepExecutionContext['endOffset']}") Long endOffset
    ) {
        return new ConcurrentFlatFileItemReader(
                objectMapper, s3Client, minio, headerExtractor
        ).withPartitionRange(key, startOffset, endOffset); // 플루언트 세터 (추가)
    }

    @Bean(name = "concurrentFlatFileWriter")
    @Primary
    public JdbcBatchItemWriter<JsonNode> concurrentFlatFileWriter(
            NamedParameterJdbcTemplate npjt
    ) {
        List<String> columns = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17");

        final String colList = columns.stream()
                .map(c -> "\"" + c + "\"")
                .collect(Collectors.joining(", "));

        final List<String> paramNames = columns.stream()
                .map(c -> "_col" + c) // 예: :_col1
                .toList();

        final String paramList = paramNames.stream()
                .map(p -> ":" + p)
                .collect(Collectors.joining(", "));

        final String sql = "INSERT INTO file_batch_test (" + colList + ") VALUES (" + paramList + ")";

        return new JdbcBatchItemWriterBuilder<JsonNode>()
                .namedParametersJdbcTemplate(npjt)
                .sql(sql)
                .itemSqlParameterSourceProvider(item -> {
                    MapSqlParameterSource p = new MapSqlParameterSource();
                    for (int i = 0; i < columns.size(); i++) {
                        String col = columns.get(i);
                        String param = "_col" + col;
                        var v = item.get(col);
                        Object val = (v == null || v.isNull()) ? null : v.asText();
                        p.addValue(param, val);
                    }
                    return p;
                })
                .assertUpdates(false)
                .build();
    }


    @Bean(name = "flatFileWorkerStep")
    public Step flatFileWorkerStep(JobRepository repo,
                                   PlatformTransactionManager tm,
                                   ConcurrentFlatFileItemReader concurrentFlatFileItemReader,
                                   @Qualifier("concurrentFlatFileWriter") JdbcBatchItemWriter<JsonNode> concurrentFlatFileWriter) {
        return new StepBuilder("flatFileWorkerStep", repo)
                .<JsonNode, JsonNode>chunk(Math.toIntExact(1_000), tm)
                .reader(concurrentFlatFileItemReader)
                .writer(concurrentFlatFileWriter)
                .build();
    }

    @Bean
    public ThreadPoolTaskExecutor partitionTaskExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(8);
        ex.setMaxPoolSize(8);
        ex.setQueueCapacity(0);
        ex.setThreadNamePrefix("part-");
        ex.initialize();
        return ex;
    }

    @Bean
    public TaskExecutorPartitionHandler partitionHandler(
            ThreadPoolTaskExecutor partitionTaskExecutor,
            @Qualifier("flatFileWorkerStep") Step flatFileWorkerStep
    ) {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setTaskExecutor(partitionTaskExecutor);
        handler.setStep(flatFileWorkerStep);
        handler.setGridSize(THREAD_SIZE);
        return handler;
    }


    @Bean(name = "concurrentFlatFileMasterStep")
    @Primary
    public Step flatFileMasterStep(JobRepository repo,
                                   MinioRangePartitioner partitioner,
                                   TaskExecutorPartitionHandler handler) {
        return new StepBuilder("flatFileMasterStep", repo)
                .partitioner("flatFileWorkerStep", partitioner)
                .partitionHandler(handler)
                .build();
    }


    @Bean(name = "concurrentFlatFileJob")
    @Primary
    public Job concurrentFlatFileJob(JobRepository repo, Step flatFileMasterStep) {
        return new JobBuilder("concurrentFlatFileJob", repo)
                .incrementer(new RunIdIncrementer())
                .start(flatFileMasterStep)  // 파티셔닝 마스터 스텝 시작
                .build();
    }
}
