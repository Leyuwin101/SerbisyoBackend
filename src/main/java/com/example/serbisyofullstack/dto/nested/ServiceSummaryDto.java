package com.example.serbisyofullstack.dto.nested;

import com.example.serbisyofullstack.model.enums.PricingType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ServiceSummaryDto {

    private Long id;

    private Long providerId;

    private Long categoryId;

    private String name;

    private String description;

    private PricingType pricingType;

    private BigDecimal basePrice;

    private Integer durationMinutes;

    private Boolean active;
}
