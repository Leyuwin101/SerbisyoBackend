package com.example.serbisyofullstack.dto.request.availability;

import io.swagger.v3.oas.annotations.media.Schema;

import com.example.serbisyofullstack.model.enums.ExceptionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request body for a provider declaring a one-day deviation from their
 * weekly schedule — a full day off, or custom hours on a specific date.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateAvailabilityExceptionRequest {
    @Schema(description = "The calendar date this exception applies to.")
    @NotNull(message = "Exception date is required")
    private LocalDate exceptionDate;

    @Schema(description = "Start of the custom working window; ignored when blocking the whole day.")
    private LocalTime startTime;

    @Schema(description = "End of the custom working window.")
    private LocalTime endTime;

    @Schema(description = "Whether the provider is available on this date (false = day off).")
    private Boolean available;

    @Schema(description = "Why the exception exists, e.g. HOLIDAY, LEAVE, CUSTOM_HOURS.")
    @NotNull(message = "Exception type is required")
    private ExceptionType exceptionType;

    @Schema(description = "Optional human-readable explanation shown to staff.")
    @Size(max = 500)
    private String reason;
}
