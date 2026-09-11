package com.example.serbisyofullstack.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a booking state transition is not allowed from the current state,
 * or the actor is not permitted to perform it.
 */
public class BookingStateException extends ApiException {

    public BookingStateException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
