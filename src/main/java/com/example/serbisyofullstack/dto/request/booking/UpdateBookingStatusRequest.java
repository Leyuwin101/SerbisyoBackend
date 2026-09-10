package com.example.serbisyofullstack.dto.request.booking;

import io.swagger.v3.oas.annotations.media.Schema;

import com.example.serbisyofullstack.model.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for changing a booking's lifecycle status (e.g. CONFIRMED,
 * COMPLETED, CANCELLED). Each transition is validated against
 * {@code BookingStatus} rules and recorded in {@code BookingStatusHistory}
 * together with {@code changedBy} and the optional note.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateBookingStatusRequest {
    @Schema(description = "The status to move the booking to.")
    @NotNull(message = "Booking status is required")
    private BookingStatus status;

    @Schema(description = "Optional explanation, stored in the status history (e.g. cancellation reason).")
    @Size(max = 1000, message = "Note must not exceed 1000 characters")
    private String note;
}
