package com.example.serbisyofullstack.dto.response.payment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.PaymentDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body acknowledging a processed payment webhook. Returning the
 * payment's new state lets the gateway verify the update took effect (many
 * gateways treat non-2xx as "retry later").
 */
@Getter
@Setter
@NoArgsConstructor
public class PaymentWebhookResponse {

    @Schema(description = "The payment after applying the webhook event.")
    private PaymentDto payment;

    @Schema(description = "Server timestamp of when the event was processed.")
    private LocalDateTime processedAt;
}
