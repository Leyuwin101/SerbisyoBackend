package com.example.serbisyofullstack.dto.response.provider;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.ProviderSummaryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body after creating the caller's provider profile. The profile
 * starts as UNVERIFIED; {@link ProviderSummaryDto} carries the current
 * verification status and rating placeholders.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateProviderProfileResponse {

    @Schema(description = "The newly created provider profile.")
    private ProviderSummaryDto provider;

    @Schema(description = "Server timestamp of creation.")
    private LocalDateTime createdAt;
}
