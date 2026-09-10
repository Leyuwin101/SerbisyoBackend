package com.example.serbisyofullstack.dto.response.booking;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.QuoteDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for submitting a booking quote. The nested {@link QuoteDto}
 * carries the amount, notes, expiry, and current status (PENDING until the
 * customer accepts).
 */
@Getter
@Setter
@NoArgsConstructor
public class BookingQuoteResponse {

    @Schema(description = "The saved quote, including its server-generated id and status.")
    private QuoteDto quote;

    @Schema(description = "Server timestamp of creation.")
    private LocalDateTime createdAt;
}
