package com.woobeee.sample.sqlbatch.config;

import com.woobeee.sample.singlecompositekey.entity.TestDataSingleChildren;
import com.woobeee.sample.sqlbatch.dto.TestRow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.*;
import org.springframework.batch.item.database.support.H2PagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Configuration
@Slf4j
@EnableBatchProcessing
public class BatchConfig {
    private final int CHUNK_SIZE = 3000;

    @Bean
    public JdbcCursorItemReader<TestRow> testReader(DataSource ds) {
        JdbcCursorItemReader<TestRow> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(ds);
        reader.setSql("""
            SELECT id, started_at, ended_at, test_data_single_id
            FROM test_data_single_children
            ORDER BY started_at ASC
    """);
        reader.setFetchSize(CHUNK_SIZE);
        reader.setRowMapper((rs, i) -> new TestRow(
                null,
                rs.getTimestamp("started_at").toLocalDateTime(),
                rs.getTimestamp("ended_at").toLocalDateTime(),
                getUuid(rs, "test_data_single_id")
        ));

        reader.setVerifyCursorPosition(false); // 커서 위치 검증 생략 -> 메모리절약
        reader.setSaveState(true);
        return reader;
    }

    // DB 호환 UUID 파서 (Postgres/H2/MySQL 모두 대응)
    private static UUID getUuid(ResultSet rs, String col) throws SQLException {
        try {
            // JDBC 4.2+ & 드라이버 지원 시
            Object o = rs.getObject(col, UUID.class);
            if (o != null) return (UUID) o;
        } catch (Throwable ignore) {}
        String s = rs.getString(col);
        return (s == null) ? null : UUID.fromString(s);
    }

    @Bean
    public ItemProcessor<TestRow, TestRow> testProcessor() {
        return in -> new TestRow(
                UUID.randomUUID(),            // 여기서 id 생성
                in.startedAt(),
                in.endedAt(),
                in.testDataSingleId()
        );
    }

    // 6-2) Writer (배치 Insert)
    @Bean
    public JdbcBatchItemWriter<TestRow> testWriter(DataSource ds) {
        var w = new JdbcBatchItemWriter<TestRow>();
        w.setDataSource(ds);
        w.setSql("""
        INSERT INTO test_data_single_children_result
            (id, started_at, ended_at, test_data_single_id)
        VALUES
            (:id, :startedAt, :endedAt, :testDataSingleId)
    """);
        w.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        w.afterPropertiesSet();
        return w;
    }

    @Bean
    public Step testChunkStep(
            JobRepository repo,
            PlatformTransactionManager tm,
            JdbcCursorItemReader<TestRow> reader,
            ItemProcessor<TestRow, TestRow> childProcessor,
            JdbcBatchItemWriter<TestRow> writer
    ) {
        return new StepBuilder("testChunkStep", repo)
                .<TestRow, TestRow>chunk(CHUNK_SIZE, tm)
                .reader(reader)
                .processor(childProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job testChunkJob(JobRepository repo, Step testChunkJo) {
        return new JobBuilder("childChunkJob", repo)
                .incrementer(new RunIdIncrementer())
                .start(testChunkJo)
                .listener(jobExecutionListener()) // ✅ Job listener 추가
                .build();
    }

    @Bean
    public JobExecutionListener jobExecutionListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                log.info("[JOB START] {} | startTime={}",
                        jobExecution.getJobInstance().getJobName(),
                        LocalDateTime.now());
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                log.info("\uD83C\uDFC1 [JOB END] {} | status={} | endTime={}",
                        jobExecution.getJobInstance().getJobName(),
                        jobExecution.getStatus(),
                        LocalDateTime.now());
            }
        };
    }
}