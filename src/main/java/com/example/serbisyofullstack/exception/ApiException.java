package com.example.serbisyofullstack.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base class for all domain exceptions. Carries the HTTP status the
 * {@link GlobalExceptionHandler} should return. Internal details are never part
 * of the message returned to the client.
 */
@Getter
public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;

    protected ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
