package com.example.serbisyofullstack.dto.request.coupon;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Admin request body for partially updating a coupon. All fields optional;
 * only the supplied ones are applied. {@code usageCount} can never be set
 * directly — it only increases through actual redemptions.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateCouponRequest {
    @Schema(description = "Updated code; must remain unique.")
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Coupon code may contain only uppercase letters, numbers, _ and -")
    private String code;

    @Schema(description = "Updated marketing description.")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Schema(description = "Updated percentage discount.")
    @DecimalMin(value = "0.01", message = "Discount percent must be greater than zero")
    @DecimalMax(value = "100.00", message = "Discount percent must not exceed 100")
    @Digits(integer = 3, fraction = 2)
    private BigDecimal discountPercent;

    @Schema(description = "Updated fixed-amount discount.")
    @DecimalMin("0.01")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal discountAmount;

    @Schema(description = "Updated minimum order total.")
    @DecimalMin("0.00")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal minimumOrderAmount;

    @Schema(description = "Updated redemption-window start.")
    private LocalDateTime validFrom;

    @Schema(description = "Updated redemption-window end.")
    private LocalDateTime validUntil;

    @Schema(description = "Updated maximum total redemptions.")
    @Min(value = 1, message = "Usage limit must be at least 1")
    private Integer usageLimit;

    @Schema(description = "Master switch; set false to disable without deleting.")
    private Boolean active;
}
