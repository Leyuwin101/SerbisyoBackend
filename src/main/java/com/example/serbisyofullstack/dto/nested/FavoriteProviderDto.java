package com.example.serbisyofullstack.dto.nested;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FavoriteProviderDto {

    private Long id;

    private Long customerId;

    private Long providerId;

    private ProviderSummaryDto provider;
}
