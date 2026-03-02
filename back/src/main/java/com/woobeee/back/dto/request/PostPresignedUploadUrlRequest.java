package com.woobeee.back.dto.request;

public record PostPresignedUploadUrlRequest(
        String fileName,
        String contentType,
        String folder
) {
}
