package com.example.serbisyofullstack.dto.nested;

import com.example.serbisyofullstack.model.enums.QuoteStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class QuoteDto {

    private Long id;

    private Long bookingId;

    private BigDecimal amount;

    private String notes;

    private LocalDateTime expiresAt;

    private QuoteStatus status;

    private LocalDateTime createdAt;
}
