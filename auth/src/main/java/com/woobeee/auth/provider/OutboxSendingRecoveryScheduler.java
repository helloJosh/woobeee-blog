package com.woobeee.auth.provider;

import com.woobeee.auth.repository.OutBoxCustomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxSendingRecoveryScheduler {


    private static final Duration STUCK_THRESHOLD = Duration.ofMinutes(10);

    private final OutBoxCustomRepository recoveryRepository;

    /**
     * SENDING 상태 stuck 메시지 복구
     */
    @Scheduled(fixedDelayString = "6000") // 1분마다
    public void recoverStuckSending() {
        LocalDateTime now = LocalDateTime.now();

        int recovered = recoveryRepository.recoverStuckSending(now, STUCK_THRESHOLD);

        if (recovered > 0) {
            log.warn("Recovered {} stuck SENDING outbox messages", recovered);
        }
    }
}
