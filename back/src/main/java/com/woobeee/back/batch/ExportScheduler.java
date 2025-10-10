package com.woobeee.back.batch;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

// ExportScheduler.java (스케줄러만 분리)
@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ExportScheduler {
    private final JobLauncher jobLauncher;

    @Qualifier("exportPostsJob")
    private final Job exportPostsJob;

    @Scheduled(cron = "0 0 * * * *") // 매 정시
    public void runExportJob() throws Exception {
        var params = new JobParametersBuilder()
                .addLong("ts", System.currentTimeMillis())
                .toJobParameters();
        log.info("Running exportPostsJob");
        jobLauncher.run(exportPostsJob, params);
    }

    @PostConstruct
    public void init() throws Exception {
        var params = new JobParametersBuilder()
                .addLong("ts", System.currentTimeMillis())
                .toJobParameters();
        log.info("Running exportPostsJob");
        jobLauncher.run(exportPostsJob, params);
    }
}