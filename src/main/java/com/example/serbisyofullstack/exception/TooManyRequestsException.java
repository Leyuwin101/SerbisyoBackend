package com.example.serbisyofullstack.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a client exceeds the rate limit on a sensitive endpoint (login,
 * registration, refresh, messaging...). Maps to 429.
 */
public class TooManyRequestsException extends ApiException {

    public TooManyRequestsException(String message) {
        super(HttpStatus.TOO_MANY_REQUESTS, message);
    }
}
