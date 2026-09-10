package com.example.serbisyofullstack.dto.request.favorite;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Request body for a customer adding a provider to (or removing a provider
 * from) their favorites list.
 */
@Getter
@Setter
public class FavoriteProviderRequest {
    @Schema(description = "The {@code ProviderProfile} to favorite or unfavorite.")
    @NotNull(message = "Provider ID is required")
    private Long providerId;
}
