package com.example.serbisyofullstack.dto.request.coupon;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Admin request body for creating a coupon. A coupon may define a
 * percentage discount, a fixed-amount discount, or both; at least one must
 * be present. {@code usageCount} and {@code createdAt} are server-managed.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateCouponRequest {
    @Schema(description = "Unique, human-typed code customers enter at checkout.")
    @NotBlank(message = "Coupon code is required")
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Coupon code may contain only uppercase letters, numbers, _ and -")
    private String code;

    @Schema(description = "Optional marketing description of the promotion.")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Schema(description = "Percentage off (e.g. 10.00 = 10%); omit for fixed-amount-only coupons.")
    @DecimalMin(value = "0.01", message = "Discount percent must be greater than zero")
    @DecimalMax(value = "100.00", message = "Discount percent must not exceed 100")
    @Digits(integer = 3, fraction = 2)
    private BigDecimal discountPercent;

    @Schema(description = "Fixed amount off the order total; omit for percentage-only coupons.")
    @DecimalMin("0.01")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal discountAmount;

    @Schema(description = "Minimum order total before the coupon can be applied.")
    @DecimalMin("0.00")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal minimumOrderAmount;

    @Schema(description = "Inclusive start of the redemption window.")
    @NotNull(message = "Valid from is required")
    private LocalDateTime validFrom;

    @Schema(description = "Inclusive end of the redemption window.")
    @NotNull(message = "Valid until is required")
    private LocalDateTime validUntil;

    @Schema(description = "Maximum total redemptions; null means unlimited.")
    @Min(value = 1, message = "Usage limit must be at least 1")
    private Integer usageLimit;

    @Schema(description = "Master switch; inactive coupons are rejected at checkout.")
    private Boolean active = true;
}
