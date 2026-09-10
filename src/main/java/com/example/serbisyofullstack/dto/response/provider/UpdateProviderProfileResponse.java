package com.example.serbisyofullstack.dto.response.provider;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.ProviderSummaryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body after partially updating the provider profile. Returns the full
 * updated record as a {@link ProviderSummaryDto}.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateProviderProfileResponse {

    @Schema(description = "The provider profile after applying the update.")
    private ProviderSummaryDto provider;

    @Schema(description = "Server timestamp of the update.")
    private LocalDateTime updatedAt;
}
