package com.example.serbisyofullstack.dto.response.payment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.PaymentDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for initiating a payment. The nested {@link PaymentDto} carries
 * the payment record; the client redirects the user to the gateway using the
 * returned checkout URL.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreatePaymentResponse {

    @Schema(description = "The created payment record (PENDING until the webhook confirms).")
    private PaymentDto payment;

    @Schema(description = "Gateway-hosted URL where the customer completes the payment.")
    private String checkoutUrl;

    @Schema(description = "Server timestamp of creation.")
    private LocalDateTime createdAt;
}
