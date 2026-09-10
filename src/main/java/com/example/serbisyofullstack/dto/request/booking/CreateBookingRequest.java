package com.example.serbisyofullstack.dto.request.booking;

import io.swagger.v3.oas.annotations.media.Schema;

import com.example.serbisyofullstack.validation.EndAfterStart;
import com.example.serbisyofullstack.validation.MaxDuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Request body for a customer creating a new booking with a provider.
 * {@code customer} is taken from the authenticated principal. Initial status
 * is PENDING until the provider quotes and accepts.
 */
@Getter
@Setter
@NoArgsConstructor
@EndAfterStart(startField = "scheduledStart", endField = "scheduledEnd", message = "Scheduled end must be after scheduled start")
@MaxDuration(startField = "scheduledStart", endField = "scheduledEnd", maxHours = 24, message = "Booking slot must not exceed 24 hours")
public class CreateBookingRequest {
    @Schema(description = "The {@code ProviderProfile} being booked.")
    @NotNull(message = "Provider ID is required")
    private Long providerId;

    @Schema(description = "Primary service driving the booking.")
    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @Schema(description = "One of the customer's saved addresses where the work happens.")
    @NotNull(message = "Address ID is required")
    private Long addressId;

    @Schema(description = "When the job should start; must be in the future.")
    @NotNull(message = "Scheduled start is required")
    @Future(message = "Scheduled start must be in the future")
    private OffsetDateTime scheduledStart;

    @Schema(description = "When the job should end; must be in the future and after the start.")
    @NotNull(message = "Scheduled end is required")
    @Future(message = "Scheduled end must be in the future")
    private OffsetDateTime scheduledEnd;

    @Schema(description = "Free-text instructions from the customer to the provider.")
    @Size(max = 2000, message = "Customer note must not exceed 2000 characters")
    private String customerNote;

    @Schema(description = "Optional additional line items; validated with {@link BookingItemRequest}.")
    @Valid
    private List<BookingItemRequest> items;
}
