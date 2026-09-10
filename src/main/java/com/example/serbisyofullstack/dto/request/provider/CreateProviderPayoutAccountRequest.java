package com.example.serbisyofullstack.dto.request.provider;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for a provider registering the payout account where earnings
 * will be sent. The {@code status} (e.g. PENDING verification with the
 * gateway) is set by the server, not the client.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateProviderPayoutAccountRequest {
    @Schema(description = "Reference of the account as known by the payment gateway (e.g. Stripe connect account id).")
    @NotBlank(message = "Gateway account reference is required")
    @Size(max = 255)
    private String gatewayAccountReference;

    @Schema(description = "How payouts are delivered, e.g. \"BANK_TRANSFER\", \"GCASH\", \"MAYA\".")
    @NotBlank(message = "Payout method is required")
    @Size(max = 100)
    private String payoutMethod;
}
