package com.woobeee.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {
    private Header header;
    private T data;

    public ApiResponse(Header header, T data) {
        this.header = header;
        this.data = data;
    }

    public ApiResponse(Header header) {
        this.header = header;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Header {
        private boolean isSuccessful;
        private String message;
        private int resultCode;
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(
                new Header(true, message, HttpStatus.OK.value()),
                data
        );
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(
                new Header(true, message, HttpStatus.OK.value())
        );
    }

    public static <T> ApiResponse<T> createSuccess(T data, String message) {
        return new ApiResponse<>(
                new Header(true, message, HttpStatus.CREATED.value()),
                data
        );
    }

    public static <T> ApiResponse<T> deleteSuccess(T data, String message) {
        return new ApiResponse<>(
                new Header(true, message, HttpStatus.NO_CONTENT.value()),
                data
        );
    }

    public static <T> ApiResponse<T> createSuccess(String message) {
        return new ApiResponse<>(
                new Header(true, message, HttpStatus.CREATED.value())
        );
    }

    public static ApiResponse<LocalDateTime> fail(HttpStatus errorCode, String message) {
        return new ApiResponse<>(
                new Header(false, message, errorCode.value()),
                LocalDateTime.now()
        );
    }
}