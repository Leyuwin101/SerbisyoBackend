package com.example.serbisyofullstack.dto.request.payment;

import io.swagger.v3.oas.annotations.media.Schema;

import com.example.serbisyofullstack.model.enums.PaymentAttemptStatus;
import com.example.serbisyofullstack.model.enums.PaymentStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for the payment gateway's webhook callbacks. Authenticity is
 * verified by signature before use; the server updates the matching
 * {@code Payment} and records a {@code PaymentAttempt} from this payload.
 */
@Getter
@Setter
@NoArgsConstructor
public class PaymentWebhookRequest {
    @Schema(description = "Our own reference stored on the Payment when it was created.")
    @NotBlank(message = "Provider reference is required")
    @Size(max = 255)
    private String providerReference;

    @Schema(description = "The gateway's attempt/reference identifier for tracing.")
    @Size(max = 255)
    private String gatewayReference;

    @Schema(description = "New overall payment status, e.g. PAID or FAILED.")
    private PaymentStatus paymentStatus;

    @Schema(description = "Status of this individual attempt, e.g. SUCCEEDED or DECLINED.")
    private PaymentAttemptStatus attemptStatus;

    @Schema(description = "Gateway-provided failure explanation when the payment was declined.")
    @Size(max = 1000)
    private String failureReason;
}
