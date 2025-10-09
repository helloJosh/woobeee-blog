package com.woobeee.sample.singlecompositekey.controller;

import com.woobeee.sample.singlecompositekey.dto.CompositeKeyTestDataResponse;
import com.woobeee.sample.singlecompositekey.dto.SingleKeyTestDataResponse;
import com.woobeee.sample.singlecompositekey.service.SingleCompositeKeyTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/single-composite-test")
@Slf4j
public class SingleCompositeKeyTestController {
    private final SingleCompositeKeyTestService service;

    @GetMapping("/single-get")
    public List<SingleKeyTestDataResponse> getSingles() {
        return service.getSingleKey();
    }

    @PostMapping("/single-post")
    public void saveSingle() {
        service.saveSingleKeyData();
    }

    @GetMapping("/composite-get")
    public List<CompositeKeyTestDataResponse> getComposite() {
        return service.getCompositeKey();
    }

    @PostMapping("/composite-post")
    public void saveComposite() {
        service.saveCompositeKeyData();
    }

}
