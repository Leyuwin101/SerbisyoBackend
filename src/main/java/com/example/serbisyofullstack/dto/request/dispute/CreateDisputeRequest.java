package com.example.serbisyofullstack.dto.request.dispute;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for a customer or provider opening a dispute against a
 * booking. {@code openedBy} is taken from the authenticated principal and
 * the dispute starts in OPEN status.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateDisputeRequest {
    @Schema(description = "The booking in dispute; must involve the authenticated party.")
    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @Schema(description = "Detailed explanation of what went wrong.")
    @NotBlank(message = "Reason is required")
    @Size(max = 3000, message = "Reason must not exceed 3000 characters")
    private String reason;
}
