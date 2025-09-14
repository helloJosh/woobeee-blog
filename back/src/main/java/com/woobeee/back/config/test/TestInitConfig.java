package com.woobeee.back.config.test;

import com.woobeee.back.entity.test.TestData;
import com.woobeee.back.entity.test.TestDataChildren;
import com.woobeee.back.entity.test.TestDataSingle;
import com.woobeee.back.entity.test.TestDataSingleChildren;
import com.woobeee.back.repository.test.TestDataChildrenRepository;
import com.woobeee.back.repository.test.TestDataRepository;
import com.woobeee.back.repository.test.TestDataSingleChildrenRepository;
import com.woobeee.back.repository.test.TestDataSingleRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional
public class TestInitConfig {
    private final TestDataChildrenRepository testDataChildrenRepository;
    private final TestDataRepository testDataRepository;

    private final TestDataSingleRepository testDataSingleRepository;
    private final TestDataSingleChildrenRepository testDataSingleChildrenRepository;

    @PostConstruct
    private void init(){
        TestData testData1 = new TestData(
                new TestData.TestDataId(UUID.randomUUID(), UUID.randomUUID()),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        TestData testData2 = new TestData(
                new TestData.TestDataId(UUID.randomUUID(), UUID.randomUUID()),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        testData1 = testDataRepository.save(testData1);
        testData2 = testDataRepository.save(testData2);

        TestDataSingle testDataSingle1 = new TestDataSingle(
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        TestDataSingle testDataSingle2 = new TestDataSingle(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        testDataSingle1 = testDataSingleRepository.save(testDataSingle1);
        testDataSingle2 = testDataSingleRepository.save(testDataSingle2);


        LocalDateTime now = LocalDateTime.now();
//        for (int i =0 ; i < 5000000; i++) {
//            TestDataChildren testDataChildren = new TestDataChildren(
//                    new TestDataChildren.TestDataChildrenId(
//                            UUID.randomUUID(),
//                            UUID.randomUUID(),
//                            testData1.getTestDataId()
//                    ),
//                    now.minusMinutes(i),
//                    now.minusMinutes(i),
//                    testData1
//            );
//            testDataChildrenRepository.save(testDataChildren);
//
//
//            TestDataSingleChildren testDataSingleChildren = new TestDataSingleChildren(
//                    now.minusMinutes(i),
//                    now.minusMinutes(i),
//                    testDataSingle1
//            );
//
//            testDataSingleChildrenRepository.save(testDataSingleChildren);
//
//        }

        for (int i = 5000000 ; i < 10000000; i++) {
            TestDataChildren testDataChildren = new TestDataChildren(
                    new TestDataChildren.TestDataChildrenId(
                            UUID.randomUUID(),
                            UUID.randomUUID(),
                            testData2.getTestDataId()
                    ),
                    now.minusMinutes(i),
                    now.minusMinutes(i),
                    testData2
            );
            testDataChildrenRepository.save(testDataChildren);

            TestDataSingleChildren testDataSingleChildren = new TestDataSingleChildren(
                    now.minusMinutes(i),
                    now.minusMinutes(i),
                    testDataSingle2
            );

            testDataSingleChildrenRepository.save(testDataSingleChildren);

        }

    }
}
