package com.example.serbisyofullstack.service.impl;

import com.example.serbisyofullstack.dto.nested.AddressDto;
import com.example.serbisyofullstack.dto.request.address.CreateAddressRequest;
import com.example.serbisyofullstack.dto.request.address.UpdateAddressRequest;
import com.example.serbisyofullstack.dto.response.address.CreateAddressResponse;
import com.example.serbisyofullstack.dto.response.address.UpdateAddressResponse;
import com.example.serbisyofullstack.exception.ResourceNotFoundException;
import com.example.serbisyofullstack.mapper.AddressMapper;
import com.example.serbisyofullstack.model.entity.Address;
import com.example.serbisyofullstack.repository.AddressRepository;
import com.example.serbisyofullstack.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Addresses owned by the authenticated user. Ownership is enforced on every
 * read, update and delete via {@code ownerId} — the owner is always the
 * authenticated principal passed by the controller, never a request field.
 */
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public CreateAddressResponse createAddress(Long ownerUserId, CreateAddressRequest request) {
        Address address = addressMapper.toEntity(request);
        address.setOwnerId(ownerUserId);
        address = addressRepository.save(address);
        CreateAddressResponse response = new CreateAddressResponse();
        response.setAddress(addressMapper.toDto(address));
        return response;
    }

    @Override
    @Transactional
    public UpdateAddressResponse updateAddress(Long ownerUserId, Long addressId, UpdateAddressRequest request) {
        Address address = loadOwnedAddress(ownerUserId, addressId);
        addressMapper.toUpdate(request, address);
        address = addressRepository.save(address);
        UpdateAddressResponse response = new UpdateAddressResponse();
        response.setAddress(addressMapper.toDto(address));
        return response;
    }

    @Override
    @Transactional
    public void deleteAddress(Long ownerUserId, Long addressId) {
        Address address = loadOwnedAddress(ownerUserId, addressId);
        addressRepository.delete(address);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDto getAddress(Long ownerUserId, Long addressId) {
        return addressMapper.toDto(loadOwnedAddress(ownerUserId, addressId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AddressDto> listAddresses(Long ownerUserId, Pageable pageable) {
        return addressRepository.findByOwnerId(ownerUserId, pageable).map(addressMapper::toDto);
    }

    private Address loadOwnedAddress(Long ownerUserId, Long addressId) {
        return addressRepository.findByIdAndOwnerId(addressId, ownerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found for the current user"));
    }
}
