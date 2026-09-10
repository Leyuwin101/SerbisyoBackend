package com.example.serbisyofullstack.dto.response.coupon;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.CouponDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body after an admin updates a coupon. Returns the full updated
 * record so the client can replace its cached copy.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateCouponResponse {

    @Schema(description = "The coupon after applying the update.")
    private CouponDto coupon;

    @Schema(description = "Server timestamp of the update.")
    private LocalDateTime updatedAt;
}
