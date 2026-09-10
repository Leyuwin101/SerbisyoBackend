package com.example.serbisyofullstack.dto.request.payment;

import io.swagger.v3.oas.annotations.media.Schema;

import com.example.serbisyofullstack.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for a customer initiating payment for a booking. The amount
 * and currency are taken from the booking's accepted quote — never from the
 * client. The response includes the gateway checkout details.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreatePaymentRequest {
    @Schema(description = "The booking being paid for; must belong to the authenticated customer.")
    @NotNull(message = "Booking Id is required")
    private Long bookingId;

    @Schema(description = "Chosen payment channel, e.g. CARD, GCASH, BANK_TRANSFER.")
    @NotNull(message = "Payment method is required")
    private PaymentMethod method;
}
