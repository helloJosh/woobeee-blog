package com.woobeee.sample.singlecompositekey.service;

import com.woobeee.sample.singlecompositekey.dto.CompositeKeyTestDataResponse;
import com.woobeee.sample.singlecompositekey.dto.SingleKeyTestDataResponse;
import com.woobeee.sample.singlecompositekey.entity.TestData;
import com.woobeee.sample.singlecompositekey.entity.TestDataChildren;
import com.woobeee.sample.singlecompositekey.entity.TestDataSingle;
import com.woobeee.sample.singlecompositekey.entity.TestDataSingleChildren;
import com.woobeee.sample.singlecompositekey.repository.TestDataChildrenRepository;
import com.woobeee.sample.singlecompositekey.repository.TestDataRepository;
import com.woobeee.sample.singlecompositekey.repository.TestDataSingleChildrenRepository;
import com.woobeee.sample.singlecompositekey.repository.TestDataSingleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class SingleCompositeKeyTestService {
    private final TestDataRepository testDataRepository;
    private final TestDataChildrenRepository testDataChildrenRepository;
    private final TestDataSingleRepository testDataSingleRepository;
    private final TestDataSingleChildrenRepository testDataSingleChildrenRepository;

    public List<SingleKeyTestDataResponse> getSingleKey() {
        List<TestDataSingleChildren> testDataSingles = testDataSingleChildrenRepository.findTop10000ByOrderByStartedAtDesc();

        return testDataSingles.stream()
                .map(entity -> new SingleKeyTestDataResponse(
                        entity.getId(),
                        entity.getStartedAt(),
                        entity.getEndedAt()
                ))
                .toList();
    }

    public List<CompositeKeyTestDataResponse> getCompositeKey() {
        List<TestDataChildren> testDataSingles = testDataChildrenRepository.findTop10000ByOrderByStartedAtDesc();

        return testDataSingles.stream()
                .map(entity -> new CompositeKeyTestDataResponse(
                        entity.getTestDataId().getId1(),
                        entity.getTestDataId().getId2(),
                        entity.getStartedAt(),
                        entity.getEndedAt()
                ))
                .toList();
    }

    public void saveSingleKeyData() {
        List<TestDataSingle> testDataSingles = testDataSingleRepository.findTop1ByOrderByStartedAtDesc();
        testDataSingles.getFirst();

        for (int i = 0 ; i < 10; i++) {
            TestDataSingleChildren testDataSingleChildren = new TestDataSingleChildren(
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    testDataSingles.getFirst().getId()
            );

            testDataSingleChildrenRepository.save(testDataSingleChildren);
        }

    }

    public void saveCompositeKeyData() {
        List<TestData> testDatas = testDataRepository.findTop1ByOrderByStartedAtDesc();


        for (int i = 0; i < 10; i++) {
            TestDataChildren testDataChildren = new TestDataChildren(
                    new TestDataChildren.TestDataChildrenId(
                            UUID.randomUUID(),
                            UUID.randomUUID()
                            //testDatas.getFirst().getTestDataId()
                            //testDatas.getFirst().getTestDataId()
                    ),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
//                    testDatas.getFirst()
                    testDatas.getFirst().getTestDataId().getId1(),
                    testDatas.getFirst().getTestDataId().getId2()
            );

            testDataChildrenRepository.save(testDataChildren);
        }
    }
}
