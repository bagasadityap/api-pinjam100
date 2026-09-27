package com.bagas.pinjam100.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {

    @Schema(example = "200")
    private Integer statusCode;

    private String message;
    private T data;
    private Object meta;
    private ErrorDetails error;

    public BaseResponse() {
    }

    public BaseResponse(
            Integer statusCode,
            String message,
            T data,
            Object meta,
            ErrorDetails error
    ) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.meta = meta;
        this.error = error;
    }

    public static <T> BaseResponse<T> success(
            HttpStatus status,
            String message,
            T data
    ) {
        return new BaseResponse<>(
                status.value(),
                message,
                data,
                null,
                null
        );
    }

    public static <T> BaseResponse<T> success(
            String message,
            T data
    ) {
        return new BaseResponse<>(
                HttpStatus.OK.value(),
                message,
                data,
                null,
                null
        );
    }

    public static <T> BaseResponse<T> error(
            HttpStatus status,
            String message,
            String errorCode,
            Object details
    ) {
        return new BaseResponse<>(
                status.value(),
                message,
                null,
                null,
                new ErrorDetails(
                        errorCode,
                        details
                )
        );
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getMeta() {
        return meta;
    }

    public void setMeta(Object meta) {
        this.meta = meta;
    }

    public ErrorDetails getError() {
        return error;
    }

    public void setError(ErrorDetails error) {
        this.error = error;
    }

    public static class ErrorDetails {

        private String code;
        private Object details;

        public ErrorDetails() {
        }

        public ErrorDetails(
                String code,
                Object details
        ) {
            this.code = code;
            this.details = details;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Object getDetails() {
            return details;
        }

        public void setDetails(Object details) {
            this.details = details;
        }
    }
}