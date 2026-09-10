package com.example.serbisyofullstack.dto.response.booking;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.BookingSummaryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for partially updating a booking (reschedule, address change,
 * or note). Returns the booking in its new state.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateBookingResponse {

    @Schema(description = "The booking after applying the update.")
    private BookingSummaryDto booking;

    @Schema(description = "Server timestamp of the update.")
    private LocalDateTime updatedAt;
}
