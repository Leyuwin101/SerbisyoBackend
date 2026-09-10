package com.example.serbisyofullstack.dto.response.coupon;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for applying a coupon at checkout. Tells the client whether the
 * coupon was accepted and, if so, how much it saves — computed by the server
 * from the trusted order total, never from client input.
 */
@Getter
@Setter
@NoArgsConstructor
public class ApplyCouponResponse {

    @Schema(description = "Whether the coupon was valid and applied.")
    private boolean applied;

    @Schema(description = "The coupon code that was evaluated.")
    private String code;

    @Schema(description = "Human-readable explanation when not applied (expired, limit reached, etc.).")
    private String message;

    @Schema(description = "Amount deducted from the order total; null when not applied.")
    private BigDecimal discountAmount;
}
