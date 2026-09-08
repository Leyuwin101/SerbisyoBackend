package com.example.serbisyofullstack.dto.nested;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProviderSummaryDto {

    private Long id;

    private Long userId;

    private String businessName;

    private String bio;

    private String verificationStatus;

    private Double averageRating;

    private Integer reviewCount;
}
