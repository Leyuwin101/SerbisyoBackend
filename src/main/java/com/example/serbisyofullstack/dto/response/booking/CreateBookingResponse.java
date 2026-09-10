package com.example.serbisyofullstack.dto.response.booking;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import com.example.serbisyofullstack.dto.nested.BookingItemDto;
import com.example.serbisyofullstack.dto.nested.BookingSummaryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for creating a booking. The nested {@link BookingSummaryDto}
 * carries the booking core; line items are returned as {@link BookingItemDto}
 * list with server-computed prices.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateBookingResponse {

    @Schema(description = "The created booking in summary form (status starts as PENDING).")
    private BookingSummaryDto booking;

    @Schema(description = "Line items with trusted, server-computed unit and total prices.")
    private List<BookingItemDto> items;
}
