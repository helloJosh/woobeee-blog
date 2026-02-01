package com.woobeee.auth.provider;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.woobeee.auth.entity.enums.EventStatus;
import com.woobeee.auth.entity.enums.EventType;
import com.woobeee.auth.repository.OutBoxCustomRepository;
import com.woobeee.auth.repository.impl.OutBoxMessageCustomRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@ExtendWith(MockitoExtension.class)
class OutboxProducerSchedulerTest {
    @Mock
    OutBoxCustomRepository outboxRepository;
    @Mock KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks OutboxProducerScheduler scheduler;

    private OutBoxMessageCustomRepositoryImpl.OutboxRow baseRow(UUID id, int attempts) {
        return new OutBoxMessageCustomRepositoryImpl.OutboxRow(
                id,
                EventType.TRIGGER,
                EventStatus.SENDING,
                "test-topic",
                "test-key",
                "{\"foo\":\"bar\"}",
                attempts,
                null,
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now(),
                null,
                null
        );
    }


    @Test
    void publish_batchEmpty_thenDoNothing() {
        given(outboxRepository.claimBatchForSend(any(), anyInt()))
                .willReturn(List.of());

        scheduler.publish();

        then(outboxRepository).should(times(1)).claimBatchForSend(any(), anyInt());
        then(outboxRepository).should(never()).markSent(any(), any());
        then(outboxRepository).should(never()).markFailed(any(), anyString(), any());
        then(outboxRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void publish_sendSuccess_thenMarkSent() throws Exception {
        UUID id = UUID.randomUUID();
        OutBoxMessageCustomRepositoryImpl.OutboxRow row = baseRow(id, 0);

        given(outboxRepository.claimBatchForSend(any(), eq(100)))
                .willReturn(List.of(row));

        // kafkaTemplate.send(...).get()을 통과시키기 위해 future를 완료 상태로 세팅
        @SuppressWarnings("unchecked")
        CompletableFuture<?> successFuture = CompletableFuture.completedFuture(null);

        given(kafkaTemplate.send("test-topic", "test-key", "{\"foo\":\"bar\"}"))
                .willReturn((CompletableFuture) successFuture);

        scheduler.publish();

        then(kafkaTemplate).should()
                .send("test-topic", "test-key", "{\"foo\":\"bar\"}");

        then(outboxRepository).should()
                .markSent(eq(id), any(LocalDateTime.class));

        then(outboxRepository).should(never())
                .markFailed(any(), any(), any());
    }

    @Test
    void publish_sendFail_thenMarkFailed_withBackoff() {
        UUID id = UUID.randomUUID();
        OutBoxMessageCustomRepositoryImpl.OutboxRow row = baseRow(id, 3); // attempts=3 → 2^3*5 = 40초

        given(outboxRepository.claimBatchForSend(any(), eq(100)))
                .willReturn(List.of(row));

        given(kafkaTemplate.send(any(), any(), any()))
                .willThrow(new RuntimeException("kafka down"));

        scheduler.publish();

        ArgumentCaptor<LocalDateTime> nextAttemptCaptor =
                ArgumentCaptor.forClass(LocalDateTime.class);

        then(outboxRepository).should()
                .markFailed(eq(id), contains("kafka down"), nextAttemptCaptor.capture());

        LocalDateTime nextAttemptAt = nextAttemptCaptor.getValue();
        LocalDateTime now = LocalDateTime.now();

        // 백오프 검증 (느슨하게)
        assertThat(nextAttemptAt).isAfter(now.plusSeconds(30));
        assertThat(nextAttemptAt).isBefore(now.plusSeconds(60));
    }
}