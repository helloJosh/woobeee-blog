package com.woobeee.sample.filebatch.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JobLauncherService {
    private final JobLauncher jobLauncher;

    @Qualifier("concurrentFlatFileJob")
    private final Job concurrentFlatFileJob;

    @Async
    public void runConcurrentJob(String path) throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("key", path)
                .addLong("chunkSize", 1_000L)
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(concurrentFlatFileJob, params);
    }


}
