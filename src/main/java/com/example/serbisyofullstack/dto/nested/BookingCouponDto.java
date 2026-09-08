package com.example.serbisyofullstack.dto.nested;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BookingCouponDto {

    private Long bookingCouponId;

    private Long bookingId;

    private Long couponId;

    private String couponCode;

    private BigDecimal discountAmount;

    private LocalDateTime appliedAt;
}
