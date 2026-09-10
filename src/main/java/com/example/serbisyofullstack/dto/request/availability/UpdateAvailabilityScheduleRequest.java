package com.example.serbisyofullstack.dto.request.availability;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for partially updating a weekly availability schedule.
 * All fields are optional; only the supplied ones are applied.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateAvailabilityScheduleRequest {
    @Schema(description = "New day of week for this window (moves the record if changed).")
    private DayOfWeek weekday;

    @Schema(description = "New start of the working window.")
    private LocalTime startTime;

    @Schema(description = "New end of the working window.")
    private LocalTime endTime;

    @Schema(description = "New IANA timezone, e.g. \"Asia/Manila\".")
    @Size(max = 100)
    private String timezone;

    @Schema(description = "Toggle the window on/off without deleting it.")
    private Boolean active;
}
