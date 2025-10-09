package com.woobeee.sample.filebatch.controller;

import com.woobeee.sample.filebatch.service.JobLauncherService;
import com.woobeee.sample.singlecompositekey.dto.SingleKeyTestDataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file-batch")
@Slf4j
public class FileController {
    private final JobLauncherService jobLauncherService;

    @GetMapping("/{key}")
    public String saveKey(@PathVariable("key") String key) throws Exception {
        jobLauncherService.runConcurrentJob(key);
        return "success";
    }
}
