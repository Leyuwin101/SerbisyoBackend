package com.example.serbisyofullstack.dto.request.booking;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Request body for a provider submitting (or updating) a price quote for a
 * pending booking. The booking's {@code quotedAmount} is synced from the
 * accepted quote; currency follows the platform default.
 */
@Getter
@Setter
@NoArgsConstructor
public class BookingQuoteRequest {
    @Schema(description = "Total quoted price for the whole booking.")
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal amount;

    @Schema(description = "Optional notes explaining the quote (materials, scope, etc.).")
    @Size(max = 2000)
    private String notes;

    @Schema(description = "When the quote lapses; the customer must accept before this time.")
    @NotNull(message = "Expiration time is required")
    @Future(message = "Expiration must be in the future")
    private OffsetDateTime expiresAt;
}
