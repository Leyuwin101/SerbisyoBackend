package com.example.serbisyofullstack.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.AddressDto;
import com.example.serbisyofullstack.dto.nested.CustomerSummaryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body after updating the customer profile. Uses nested DTOs:
 * {@link CustomerSummaryDto} for profile fields and {@link AddressDto} for the
 * (possibly new) default address.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateCustomerProfileResponse {

    @Schema(description = "Updated customer profile fields (display name, avatar).")
    private CustomerSummaryDto customer;

    @Schema(description = "Current default address; null when none is set.")
    private AddressDto defaultAddress;

    @Schema(description = "Server timestamp of the update.")
    private LocalDateTime updatedAt;
}
