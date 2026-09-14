package com.bagas.pinjam100.exception;

import jakarta.annotation.Nullable;
import org.springframework.security.core.Authentication;

import java.io.Serial;

public class AuthenticationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2018827003361503060L;

    private @Nullable Authentication authenticationRequest;

    public AuthenticationException(@Nullable String msg, Throwable cause) {
        super(msg, cause);
    }

    public AuthenticationException(@Nullable String msg) {
        super(msg);
    }
}
