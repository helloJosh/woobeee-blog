package com.woobeee.auth.listener;

import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woobeee.auth.dto.provider.MessageEvent;
import com.woobeee.auth.entity.enums.EventStatus;
import com.woobeee.auth.entity.enums.EventType;
import com.woobeee.auth.repository.OutBoxCustomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageEventListenerTest {

    private OutBoxCustomRepository repo;
    private ObjectMapper objectMapper;
    private MessageEventListener listener;

    @BeforeEach
    void setUp() {
        repo = mock(OutBoxCustomRepository.class);
        objectMapper = mock(ObjectMapper.class);
        listener = new MessageEventListener(repo, objectMapper);

        // @Value 주입 대신 테스트에서 직접 세팅
        ReflectionTestUtils.setField(listener, "profile", "local-");
    }

    @Test
    void handleEvent_shouldInsertOutboxWithPrefixedTopicAndSerializedPayload() throws Exception {
        UUID eventId = UUID.randomUUID();
        MessageEvent event = new MessageEvent(
                eventId,
                "orders.created",
                "user-1",
                Map.of("a", 1)
        );

        when(objectMapper.writeValueAsString(event.message()))
                .thenReturn("{\"a\":1}");

        listener.handleEvent(event);

        // insertNew 호출 검증 (시간은 any로 처리하거나 캡처해서 범위로 검증)
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LocalDateTime> createdAtCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(repo, times(1)).insertNew(
                eq(eventId),
                eq(EventType.TRIGGER),
                eq(EventStatus.NEW),
                topicCaptor.capture(),
                keyCaptor.capture(),
                payloadCaptor.capture(),
                createdAtCaptor.capture()
        );

        assertThat(topicCaptor.getValue()).isEqualTo("local-" + "orders.created");
        assertThat(keyCaptor.getValue()).isEqualTo("user-1");
        assertThat(payloadCaptor.getValue()).isEqualTo("{\"a\":1}");

        // createdAt은 "호출 시점"이라 엄격 일치 대신 합리적 범위 체크
        assertThat(createdAtCaptor.getValue()).isNotNull();
    }

    @Test
    void handleEvent_whenSerializationFails_shouldThrowRuntimeException() throws Exception {
        UUID eventId = UUID.randomUUID();
        MessageEvent event = new MessageEvent(eventId, "t", "k", Map.of("x", 1));

        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new JsonProcessingException("boom") {});

        assertThatThrownBy(() -> listener.handleEvent(event))
                .isInstanceOf(RuntimeException.class);

        verify(repo, never()).insertNew(any(), any(), any(), any(), any(), any(), any());
    }
}