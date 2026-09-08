package com.example.serbisyofullstack.dto.nested;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BookingSummaryDto{

    private Long id;

    private Long customerId;

    private Long providerId;

    private Long serviceId;

    private LocalDateTime scheduledStart;

    private LocalDateTime scheduledEnd;

    private String status;

    private BigDecimal quotedAmount;
}
