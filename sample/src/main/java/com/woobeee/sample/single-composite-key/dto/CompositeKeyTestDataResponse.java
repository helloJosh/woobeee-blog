package com.woobeee.back.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompositeKeyTestDataResponse {
    private UUID id1;
    private UUID id2;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
