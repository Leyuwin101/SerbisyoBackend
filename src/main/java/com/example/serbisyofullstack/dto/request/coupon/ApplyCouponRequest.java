package com.example.serbisyofullstack.dto.request.coupon;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for applying a coupon to a booking during checkout. The
 * server validates the code, its window, usage limits, and the minimum
 * order amount, then returns the computed discount.
 */
@Getter
@Setter
@NoArgsConstructor
public class ApplyCouponRequest {
    @Schema(description = "The coupon code the customer typed in.")
    @NotBlank(message = "Coupon code is required")
    @Size(max = 50)
    private String code;
}
