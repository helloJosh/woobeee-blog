package com.woobeee.auth.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woobeee.auth.dto.provider.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageEventListener {
    private final static String EXTRACT_TRIGGER_TOPIC = "-extract-trigger.dm-batch";

    @Value("${spring.config.activate.on-profile}")
    private String profile;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDatasetSavedEvent(MessageEvent event) {
        kafkaTemplate.send(
                        profile + EXTRACT_TRIGGER_TOPIC,
                        Message.signInAfterMessage(event.getMessage(), objectMapper)
                )
                .whenComplete((result, ex) -> {
                            if (ex != null) {
                                log.error("메시지 전송 실패: topic={}, key={}", EXTRACT_TRIGGER_TOPIC, ex);
                                throw new RuntimeException(ex);
                            } else {
                                log.info("메시지 전송 성공: topic={}, partition={}, offset={}",
                                        result.getRecordMetadata().topic(),
                                        result.getRecordMetadata().partition(),
                                        result.getRecordMetadata().offset());
                            }
                        }
                );
    }
}
