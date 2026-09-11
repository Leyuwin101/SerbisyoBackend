package com.example.serbisyofullstack.service;

import com.example.serbisyofullstack.dto.request.address.CreateAddressRequest;
import com.example.serbisyofullstack.dto.request.address.UpdateAddressRequest;
import com.example.serbisyofullstack.dto.nested.AddressDto;
import com.example.serbisyofullstack.dto.response.address.CreateAddressResponse;
import com.example.serbisyofullstack.dto.response.address.UpdateAddressResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Addresses owned by the authenticated user. Ownership is enforced on every
 * read/update/delete.
 */
public interface AddressService {

    CreateAddressResponse createAddress(Long ownerUserId, CreateAddressRequest request);

    UpdateAddressResponse updateAddress(Long ownerUserId, Long addressId, UpdateAddressRequest request);

    void deleteAddress(Long ownerUserId, Long addressId);

    AddressDto getAddress(Long ownerUserId, Long addressId);

    Page<AddressDto> listAddresses(Long ownerUserId, Pageable pageable);
}
