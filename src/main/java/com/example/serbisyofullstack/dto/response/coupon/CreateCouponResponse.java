package com.example.serbisyofullstack.dto.response.coupon;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.CouponDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body after an admin creates a coupon. Returns the stored coupon
 * (with generated id) as a {@link CouponDto}; {@code usageCount} starts at
 * zero.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateCouponResponse {

    @Schema(description = "The created coupon, including its server-generated id.")
    private CouponDto coupon;

    @Schema(description = "Server timestamp of creation.")
    private LocalDateTime createdAt;
}
