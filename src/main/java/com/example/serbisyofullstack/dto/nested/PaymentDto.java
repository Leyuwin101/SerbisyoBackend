package com.example.serbisyofullstack.dto.nested;

import com.example.serbisyofullstack.model.enums.PaymentMethod;
import com.example.serbisyofullstack.model.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PaymentDto {

    private Long id;

    private Long bookingId;

    private BigDecimal amount;

    private String currency;

    private PaymentStatus status;

    private PaymentMethod method;

    private String providerReference;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
