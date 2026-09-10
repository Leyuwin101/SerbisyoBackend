package com.example.serbisyofullstack.dto.request.messaging;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for a customer starting a conversation with a provider,
 * optionally tied to a booking. The customer side is taken from the
 * authenticated principal; {@code providerId} must be an existing provider.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateConversationRequest {
    @Schema(description = "Optional booking the conversation is about.")
    private Long bookingId;

    @Schema(description = "The provider to open the conversation with.")
    @NotNull(message = "Provider ID is required")
    private Long providerId;
}
