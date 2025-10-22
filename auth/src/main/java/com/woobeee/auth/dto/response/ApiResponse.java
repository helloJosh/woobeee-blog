package com.woobeee.auth.dto.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        Header header,
        T data
) {

    public record Header(
            boolean isSuccessful,
            String message,
            int resultCode
    ) {}

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(
                new Header(true, message, HttpStatus.OK.value()),
                data
        );
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(
                new Header(true, message, HttpStatus.OK.value()),
                null
        );
    }

    public static <T> ApiResponse<T> createSuccess(T data, String message) {
        return new ApiResponse<>(
                new Header(true, message, HttpStatus.CREATED.value()),
                data
        );
    }

    public static <T> ApiResponse<T> createSuccess(String message) {
        return new ApiResponse<>(
                new Header(true, message, HttpStatus.CREATED.value()),
                null
        );
    }

    public static <T> ApiResponse<T> deleteSuccess(T data, String message) {
        return new ApiResponse<>(
                new Header(true, message, HttpStatus.NO_CONTENT.value()),
                data
        );
    }

    public static ApiResponse<LocalDateTime> fail(HttpStatus errorCode, String message) {
        return new ApiResponse<>(
                new Header(false, message, errorCode.value()),
                LocalDateTime.now()
        );
    }
}