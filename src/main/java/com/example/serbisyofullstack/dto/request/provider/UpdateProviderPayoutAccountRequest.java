package com.example.serbisyofullstack.dto.request.provider;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for a provider updating their registered payout account.
 * Both fields are optional; only the supplied ones are applied. The
 * account {@code status} is re-evaluated by the server after the change.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateProviderPayoutAccountRequest {
    @Schema(description = "New gateway account reference, e.g. after re-linking the payout account.")
    @NotBlank(message = "Gateway account reference is required")
    @Size(max = 255)
    private String gatewayAccountReference;

    @Schema(description = "New payout delivery method, e.g. \"BANK_TRANSFER\", \"GCASH\".")
    @Size(max = 100)
    private String payoutMethod;
}
