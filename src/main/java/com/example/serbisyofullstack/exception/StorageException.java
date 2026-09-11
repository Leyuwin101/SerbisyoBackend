package com.example.serbisyofullstack.exception;

import org.springframework.http.HttpStatus;

public class StorageException extends ApiException {

    public StorageException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public StorageException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
        initCause(cause);
    }
}
