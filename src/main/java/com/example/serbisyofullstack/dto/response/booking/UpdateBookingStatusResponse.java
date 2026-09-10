package com.example.serbisyofullstack.dto.response.booking;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.BookingSummaryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for a booking status change. The booking echoes its new status;
 * the transition is also recorded in {@code BookingStatusHistory}.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateBookingStatusResponse {

    @Schema(description = "The booking with its new status.")
    private BookingSummaryDto booking;

    @Schema(description = "Server timestamp of the status change.")
    private LocalDateTime changedAt;
}
