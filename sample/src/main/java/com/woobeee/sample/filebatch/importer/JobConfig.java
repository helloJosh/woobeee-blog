package com.woobeee.sample.filebatch.importer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woobeee.sample.filebatch.config.MinioConfig;
import com.woobeee.sample.filebatch.support.FlatFileHeaderExtractor;
import com.woobeee.sample.filebatch.support.FlatFileIndexer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class JobConfig {
    private final int CHUNK_SIZE = 10;

    private final ObjectMapper objectMapper;
    private final S3Client s3Client;
    private final MinioConfig.MinioProperties minio;
    private final FlatFileHeaderExtractor headerExtractor;
    private final FlatFileIndexer indexer;

    private final JobRegistry jobRegistry;

    @Bean
    public FlatFileItemReader flatFileItemReader() {
        return new FlatFileItemReader(
                objectMapper,
                s3Client,
                minio,
                headerExtractor,
                indexer,
                ",",
                CHUNK_SIZE
        );
    }

    @Bean
    public JdbcBatchItemWriter<JsonNode> flatFileWriter(
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


    @Bean(name = "flatFileStep")
    @Primary
    public Step flatFileStep(JobRepository repo,
                             PlatformTransactionManager tm,
                             FlatFileItemReader reader,
                             JdbcBatchItemWriter<JsonNode> writer) {

        return new StepBuilder("flatFileStep", repo)
                .<JsonNode, JsonNode>chunk(CHUNK_SIZE, tm)   // chunk 크기 조절 가능
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean(name = "flatFileJob")
    @Primary
    public Job flatFileJob(JobRepository repo, Step flatFileStep) {
        return new JobBuilder("flatFileJob", repo)
                .incrementer(new RunIdIncrementer())
                .start(flatFileStep)
                .build();
    }
}
