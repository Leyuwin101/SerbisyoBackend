package com.example.serbisyofullstack.dto.nested;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CouponDto {

    private Long id;

    private String code;

    private String description;

    private BigDecimal discountPercent;

    private BigDecimal discountAmount;

    private LocalDateTime validFrom;

    private LocalDateTime validUntil;

    private Integer usageLimit;

    private Integer usageCount;

    private Boolean active;
}
