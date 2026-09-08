package com.example.serbisyofullstack.dto.nested;

import com.example.serbisyofullstack.model.enums.PayoutAccountStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProviderPayoutAccountDto {

    private Long id;

    private Long providerId;

    private String providerReference;

    private PayoutAccountStatus status;

    private Boolean readyForPayout;
}
