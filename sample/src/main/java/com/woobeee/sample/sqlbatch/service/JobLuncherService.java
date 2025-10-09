package com.woobeee.sample.sqlbatch.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class JobLuncherService {
    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final JobExplorer jobExplorer;

    @Qualifier("testChunkJob")
    private final Job childChunkJob;

    /**
     * Job 실행 메서드
     */
    @Async
    public void runChildJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder(jobExplorer)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(childChunkJob, jobParameters);

        } catch (Exception e) {
            log.error("[JOB ERROR]", e);
        }
    }
}
