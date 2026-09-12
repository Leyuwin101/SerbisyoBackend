package com.example.serbisyofullstack.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.serbisyofullstack.api.PaginationGuard;
import com.example.serbisyofullstack.dto.nested.AddressDto;
import com.example.serbisyofullstack.dto.request.address.CreateAddressRequest;
import com.example.serbisyofullstack.dto.request.address.UpdateAddressRequest;
import com.example.serbisyofullstack.dto.response.address.CreateAddressResponse;
import com.example.serbisyofullstack.dto.response.address.UpdateAddressResponse;
import com.example.serbisyofullstack.security.CurrentUserService;
import com.example.serbisyofullstack.service.AddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Address book of the authenticated user. Ownership is enforced in
 * {@link AddressService}; the owner id never comes from the client.
 */
@Tag(name = "Addresses", description = "Address book of the authenticated user")
@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final CurrentUserService currentUserService;

    @Operation(summary = "List the authenticated user's addresses")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Addresses retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping
    public ResponseEntity<Page<AddressDto>> listAddresses(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(addressService.listAddresses(
                currentUserService.getCurrentUserId(), PaginationGuard.cap(pageable)));
    }

    @Operation(summary = "Get a single address by id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Address retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Address does not belong to the user"),
        @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AddressDto> getAddress(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getAddress(
                currentUserService.getCurrentUserId(), id));
    }

    @Operation(summary = "Create a new address")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Address created"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping
    public ResponseEntity<CreateAddressResponse> createAddress(
            @Valid @RequestBody CreateAddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.createAddress(currentUserService.getCurrentUserId(), request));
    }

    @Operation(summary = "Update an existing address")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Address updated"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Address does not belong to the user"),
        @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UpdateAddressResponse> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAddressRequest request) {
        return ResponseEntity.ok(addressService.updateAddress(
                currentUserService.getCurrentUserId(), id, request));
    }

    @Operation(summary = "Delete an address")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Address deleted"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Address does not belong to the user"),
        @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(currentUserService.getCurrentUserId(), id);
        return ResponseEntity.noContent().build();
    }
}
