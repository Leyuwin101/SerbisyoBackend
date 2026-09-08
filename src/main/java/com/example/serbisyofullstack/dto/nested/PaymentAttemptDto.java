package com.example.serbisyofullstack.dto.nested;

import com.example.serbisyofullstack.model.enums.PaymentAttemptStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PaymentAttemptDto {

    private Long paymentAttemptId;

    private Long paymentId;

    private PaymentAttemptStatus status;

    private String gatewayReference;

    private String failureReason;

    private LocalDateTime attemptedAt;
}
