package com.woobeee.sample.sqlbatch.dto;
import java.time.LocalDateTime;
import java.util.UUID;

public record TestRow(
        UUID id,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        UUID testDataSingleId
) {}
