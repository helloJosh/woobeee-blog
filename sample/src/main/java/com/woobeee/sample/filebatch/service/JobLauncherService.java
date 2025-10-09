package com.woobeee.sample.filebatch.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JobLauncherService {
    private final JobLauncher jobLauncher;
    private final Job flatFileJob;

    @Async
    public void runJob(String path) throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("key", path) // S3 key
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(flatFileJob, params);
    }
}
