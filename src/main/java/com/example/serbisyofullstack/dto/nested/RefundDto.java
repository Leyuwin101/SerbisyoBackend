package com.example.serbisyofullstack.dto.nested;

import com.example.serbisyofullstack.model.enums.RefundStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RefundDto {

    private Long refundId;

    private Long paymentId;

    private BigDecimal amount;

    private String reason;

    private RefundStatus status;

    private String providerReference;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
