package com.example.serbisyofullstack.dto.response.provider;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.ProviderPayoutAccountDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body after registering a payout account. The nested
 * {@link ProviderPayoutAccountDto} exposes the account's verification status;
 * the gateway reference is echoed so the provider can confirm it.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateProviderPayoutAccountResponse {

    @Schema(description = "The created payout account, including its verification status.")
    private ProviderPayoutAccountDto payoutAccount;

    @Schema(description = "Server timestamp of creation.")
    private LocalDateTime createdAt;
}
