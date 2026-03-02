package com.woobeee.auth.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PostOauthSignUpRequest(
        String idToken,
        String nickname,
        String instargramId,
        String preferredRegion,
        String introText,
        String idealTypeText,
        Integer yearsOfTraining,
        List<UUID> tagIds,
        InbodyRecordRequest inbodyRecord,
        StrengthRecordRequest strengthRecord,
        RunningRecordRequest runningRecord
) {
    public record InbodyRecordRequest(
            Integer weight,
            Integer skeletalMuscle,
            Integer bodyFatPercent,
            Integer visceralFat,
            LocalDateTime measuredAt,
            Boolean isVerified,
            String imageUrl
    ) {
    }

    public record StrengthRecordRequest(
            Integer squat,
            Integer bench,
            Integer deadlift,
            Integer recordedAt,
            Boolean isVerified
    ) {
    }

    public record RunningRecordRequest(
            Integer records,
            LocalDateTime recordedAt,
            Boolean isVerified
    ) {
    }
}
