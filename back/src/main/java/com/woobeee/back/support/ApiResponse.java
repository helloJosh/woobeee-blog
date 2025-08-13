package com.woobeee.back.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {
    private Header header;
    private Body<T> body;

    public ApiResponse(Header header, Body<T> body) {
        this.header = header;
        this.body = body;
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

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Body<T> {
        private T data;

        public Body(T data) {
            this.data = data;
        }
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(
                new Header(true,message, HttpStatus.OK.value()),
                new Body<>(data)
        );
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(
                new Header(true,message, HttpStatus.OK.value())
        );
    }

    public static <T> ApiResponse<T> createSuccess(T data,String message) {
        return new ApiResponse<>(
                new Header(true,message, HttpStatus.CREATED.value()),
                new Body<>(data)
        );
    }
    public static <T> ApiResponse<T> deleteSuccess(T data, String message) {
        return new ApiResponse<>(
                new Header(true,message, HttpStatus.NO_CONTENT.value()),
                new Body<>(data)
        );
    }

    public static <T> ApiResponse<T> createSuccess(String message) {
        return new ApiResponse<>(
                new Header(true, message, HttpStatus.CREATED.value())
        );
    }


    public static <T> ApiResponse<T> fail(HttpStatus errorCode, Body<T> body, String message) {
        return new ApiResponse<>(
                new Header(
                        false,
                        message,
                        errorCode.value()
                ),
                body
        );
    }

}