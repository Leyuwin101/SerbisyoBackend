package com.example.serbisyofullstack.dto.response.availability;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.AvailabilityExceptionDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for partially updating a one-day availability exception.
 * Returns the full updated record.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateAvailabilityExceptionResponse {

    @Schema(description = "The exception after applying the update.")
    private AvailabilityExceptionDto exception;

    @Schema(description = "Server timestamp of the update.")
    private LocalDateTime updatedAt;
}
