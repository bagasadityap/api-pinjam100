package com.bagas.pinjam100.exception;

public class InvalidFcmTokenException extends RuntimeException {

    public InvalidFcmTokenException(String message) {
        super(message);
    }

    public InvalidFcmTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}