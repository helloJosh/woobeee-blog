package com.woobeee.back.dto.response;

public record GetPresignedUploadUrlResponse(
        String uploadUrl,
        String objectKey,
        Long expiresInSeconds
) {
}
