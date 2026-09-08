package com.example.serbisyofullstack.dto.nested;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CustomerSummaryDto {

    private Long id;

    private Long userId;

    private String displayName;

    private String avatarUrl;
}
