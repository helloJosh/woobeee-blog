package com.woobeee.sample.sqlbatch.controller;

import com.woobeee.sample.singlecompositekey.dto.SingleKeyTestDataResponse;
import com.woobeee.sample.sqlbatch.service.JobLuncherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sql-batch")
@Slf4j
public class BatchJobController {
    private final JobLuncherService jobLuncherService;

    @GetMapping("/job")
    public String getStartJob() {
        jobLuncherService.runChildJob();

        return "success";
    }
}
