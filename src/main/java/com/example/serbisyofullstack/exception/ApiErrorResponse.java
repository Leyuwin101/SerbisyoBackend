package com.example.serbisyofullstack.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * Stable API error contract. Never carries stack traces, SQL messages, class
 * names or secrets.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final Instant timestamp;
    private final Map<String, String> fieldErrors;
}
