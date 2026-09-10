package com.example.serbisyofullstack.dto.response.availability;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.AvailabilityScheduleDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for creating a weekly availability window. Returns the stored
 * window (with generated id) as an {@link AvailabilityScheduleDto}.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateAvailabilityScheduleResponse {

    @Schema(description = "The saved weekly window, including its server-generated id.")
    private AvailabilityScheduleDto schedule;

    @Schema(description = "Server timestamp of creation.")
    private LocalDateTime createdAt;
}
