package com.woobeee.sample.singlecompositekey.config;

import com.woobeee.sample.singlecompositekey.entity.TestData;
import com.woobeee.sample.singlecompositekey.entity.TestDataSingle;
import com.woobeee.sample.singlecompositekey.entity.TestDataSingleChildren;
import com.woobeee.sample.singlecompositekey.repository.TestDataChildrenRepository;
import com.woobeee.sample.singlecompositekey.repository.TestDataRepository;
import com.woobeee.sample.singlecompositekey.repository.TestDataSingleChildrenRepository;
import com.woobeee.sample.singlecompositekey.repository.TestDataSingleRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional
public class TestInitConfig {
    private final TestDataChildrenRepository testDataChildrenRepository;
    private final TestDataRepository testDataRepository;

    private final TestDataSingleRepository testDataSingleRepository;
    private final TestDataSingleChildrenRepository testDataSingleChildrenRepository;

//    @PostConstruct
//    private void init(){
//        TestData testData1 = new TestData(
//                new TestData.TestDataId(UUID.randomUUID(), UUID.randomUUID()),
//                LocalDateTime.now(),
//                LocalDateTime.now()
//        );
//        testData1 = testDataRepository.save(testData1);
//
//        TestDataSingle testDataSingle1 = new TestDataSingle(
//                LocalDateTime.now(),
//                LocalDateTime.now()
//        );
//
//
//        testDataSingle1 = testDataSingleRepository.save(testDataSingle1);
//
//        LocalDateTime now = LocalDateTime.now();
//
//        for (int i =0 ; i < 100; i++) {
//            List<TestDataSingleChildren> add = new ArrayList<>();
//            for (int j = 0; j < 100000; j++) {
//                TestDataSingleChildren testDataSingleChildren = new TestDataSingleChildren(
//                        now.minusMinutes(i),
//                        now.minusMinutes(i),
//                        testDataSingle1.getId()
//                );
//                add.add(testDataSingleChildren);
//            }
//
//            testDataSingleChildrenRepository.saveAll(add);
//        }
//    }
}
