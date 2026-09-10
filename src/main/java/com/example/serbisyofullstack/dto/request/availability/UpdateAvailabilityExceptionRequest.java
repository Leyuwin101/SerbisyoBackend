package com.example.serbisyofullstack.dto.request.availability;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.serbisyofullstack.model.enums.ExceptionType;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for partially updating a one-day availability exception.
 * All fields are optional; only the supplied ones are applied.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateAvailabilityExceptionRequest {
    @Schema(description = "New calendar date for this exception.")
    private LocalDate exceptionDate;

    @Schema(description = "New start of the custom working window.")
    private LocalTime startTime;

    @Schema(description = "New end of the custom working window.")
    private LocalTime endTime;

    @Schema(description = "Toggle between day-off and custom-hours availability.")
    private Boolean available;

    @Schema(description = "New exception category, e.g. HOLIDAY, LEAVE, CUSTOM_HOURS.")
    private ExceptionType exceptionType;

    @Schema(description = "Updated explanation for the exception.")
    @Size(max = 500)
    private String reason;
}
