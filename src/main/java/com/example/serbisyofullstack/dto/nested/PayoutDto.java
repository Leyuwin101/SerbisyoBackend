package com.example.serbisyofullstack.dto.nested;

import com.example.serbisyofullstack.model.enums.PayoutStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PayoutDto {

    private Long id;

    private Long providerId;

    private BigDecimal amount;

    private PayoutStatus status;

    private String payoutReference;

    private LocalDateTime periodStart;

    private LocalDateTime periodEnd;

    private LocalDateTime createdAt;
}
