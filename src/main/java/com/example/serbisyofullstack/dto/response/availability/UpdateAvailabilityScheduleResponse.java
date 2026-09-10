package com.example.serbisyofullstack.dto.response.availability;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.AvailabilityScheduleDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for partially updating a weekly availability window. Returns
 * the full updated record.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateAvailabilityScheduleResponse {

    @Schema(description = "The weekly window after applying the update.")
    private AvailabilityScheduleDto schedule;

    @Schema(description = "Server timestamp of the update.")
    private LocalDateTime updatedAt;
}
