package com.example.serbisyofullstack.dto.request.booking;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A single line item inside a {@link CreateBookingRequest}. Nested so the
 * booking payload stays self-contained; the server re-prices each item from
 * the trusted {@code Service} record rather than trusting client prices.
 */
@Getter
@Setter
@NoArgsConstructor
public class BookingItemRequest {
    @Schema(description = "The {@code Service} being booked on this line.")
    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @Schema(description = "How many units of the service; used with per-unit pricing.")
    @Min(value = 1)
    @Max(value = 100)
    private Integer quantity;
}
