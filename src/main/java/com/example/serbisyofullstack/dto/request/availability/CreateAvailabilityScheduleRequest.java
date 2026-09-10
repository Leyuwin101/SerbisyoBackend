package com.example.serbisyofullstack.dto.request.availability;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Request body for a provider defining their recurring weekly working hours.
 * One record per weekday; specific off-days or custom hours go through
 * {@code AvailabilityException} instead.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateAvailabilityScheduleRequest {
    @Schema(description = "Day of week this window applies to (MONDAY..SUNDAY).")
    @NotNull(message = "Weekday is required")
    private DayOfWeek weekday;

    @Schema(description = "Start of the working window, in the given timezone.")
    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @Schema(description = "End of the working window; must be after {@code startTime}.")
    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @Schema(description = "IANA timezone the times are expressed in, e.g. \"Asia/Manila\".")
    @NotBlank(message = "Timezone is required")
    @Size(max = 100)
    private String timezone;

    @Schema(description = "Whether this weekly window is currently in effect.")
    private boolean active = true;
}
