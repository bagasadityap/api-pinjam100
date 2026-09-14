package com.bagas.pinjam100.exception;

import jakarta.annotation.Nullable;
import org.springframework.security.core.Authentication;

import java.io.Serial;

public class ForbiddenException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 2018827003361503061L;

    private @Nullable Authentication authenticationRequest;

    public ForbiddenException(@Nullable String msg, Throwable cause) {
        super(msg, cause);
    }

    public ForbiddenException(@Nullable String msg) {
        super(msg);
    }
}