package com.example.serbisyofullstack.dto.response.address;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.AddressDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for partially updating an address. Returns the full updated
 * record so the client can replace its cached copy.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateAddressResponse {

    @Schema(description = "The address after applying the update.")
    private AddressDto address;

    @Schema(description = "Server timestamp of the update.")
    private LocalDateTime updatedAt;
}
