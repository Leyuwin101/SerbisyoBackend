package com.example.serbisyofullstack.dto.response.provider;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.ProviderPayoutAccountDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body after updating a provider's payout account. The account's
 * status may be re-evaluated after the change and is reflected in the nested
 * {@link ProviderPayoutAccountDto}.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateProviderPayoutAccountResponse {

    @Schema(description = "The payout account after applying the update.")
    private ProviderPayoutAccountDto payoutAccount;

    @Schema(description = "Server timestamp of the update.")
    private LocalDateTime updatedAt;
}
