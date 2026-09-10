package com.example.serbisyofullstack.dto.response.address;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.AddressDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for creating an address. Returns the persisted address (with
 * its generated {@code id}) as an {@link AddressDto}.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateAddressResponse {

    @Schema(description = "The saved address, including its server-generated id.")
    private AddressDto address;

    @Schema(description = "Server timestamp of creation.")
    private LocalDateTime createdAt;
}
