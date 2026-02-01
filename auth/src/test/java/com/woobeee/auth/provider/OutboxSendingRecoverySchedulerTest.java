package com.woobeee.auth.provider;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.Duration;
import java.time.LocalDateTime;

import com.woobeee.auth.repository.OutBoxCustomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OutboxSendingRecoverySchedulerTest {

    @Mock
    OutBoxCustomRepository recoveryRepository;

    @InjectMocks
    OutboxSendingRecoveryScheduler scheduler;

    @Test
    void recoverStuckSending_callsRepositoryWithThreshold() {
        given(recoveryRepository.recoverStuckSending(any(LocalDateTime.class), any(Duration.class)))
                .willReturn(0);

        scheduler.recoverStuckSending();

        then(recoveryRepository).should(times(1))
                .recoverStuckSending(any(LocalDateTime.class), eq(Duration.ofMinutes(10)));

        then(recoveryRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void recoverStuckSending_whenRecoveredPositive_stillJustCallsRepository() {
        given(recoveryRepository.recoverStuckSending(any(LocalDateTime.class), any(Duration.class)))
                .willReturn(3);

        scheduler.recoverStuckSending();

        then(recoveryRepository).should(times(1))
                .recoverStuckSending(any(LocalDateTime.class), eq(Duration.ofMinutes(10)));

        then(recoveryRepository).shouldHaveNoMoreInteractions();
    }
}