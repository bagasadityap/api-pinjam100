package com.bagas.pinjam100.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BaseResponse<T> {

    private Integer statusCode;
    private String message;
    private T data;
    private MetaResponse meta;
    private ErrorResponse error;

    public static <T> BaseResponse<T> success(
            Integer statusCode,
            String message,
            T data
    ) {
        return BaseResponse.<T>builder()
                .statusCode(statusCode)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> success(
            String message,
            T data
    ) {
        return success(200, message, data);
    }

    public static BaseResponse<Void> success(String message) {
        return success(200, message, null);
    }
}