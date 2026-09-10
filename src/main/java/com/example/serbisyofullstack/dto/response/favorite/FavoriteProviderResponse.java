package com.example.serbisyofullstack.dto.response.favorite;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.FavoriteProviderDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for favoriting a provider. The nested
 * {@link FavoriteProviderDto} includes a {@code ProviderSummaryDto} so the
 * favorites list can render without extra lookups.
 */
@Getter
@Setter
@NoArgsConstructor
public class FavoriteProviderResponse {

    @Schema(description = "The created favorite entry with the provider's summary.")
    private FavoriteProviderDto favorite;

    @Schema(description = "Server timestamp of creation.")
    private LocalDateTime createdAt;
}
