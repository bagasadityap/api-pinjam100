package com.bagas.pinjam100.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {

    @Schema(example = "NIP atau password salah")
    private String message;

    @Schema(example = "Unauthorized")
    private String error;

    @Schema(example = "401")
    private Integer status;

    @Schema(example = "2006-09-21T03:29:45.746295065Z")
    private String timestamp;

    public ErrorResponse() {}

    public ErrorResponse(String message, String error, Integer status, String timestamp) {
        this.message = message;
        this.error = error;
        this.status = status;
        this.timestamp = timestamp;
    }
}