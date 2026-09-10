package com.example.serbisyofullstack.dto.response.availability;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.AvailabilityExceptionDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for creating a one-day availability exception. Returns the
 * stored exception (with generated id) as an {@link AvailabilityExceptionDto}.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateAvailabilityExceptionResponse {

    @Schema(description = "The saved exception, including its server-generated id.")
    private AvailabilityExceptionDto exception;

    @Schema(description = "Server timestamp of creation.")
    private LocalDateTime createdAt;
}
