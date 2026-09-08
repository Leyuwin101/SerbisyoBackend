package com.example.serbisyofullstack.dto.nested;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ReviewSummaryDto {

    private Long id;

    private Long bookingId;

    private Long customerId;

    private Long providerId;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;
}
