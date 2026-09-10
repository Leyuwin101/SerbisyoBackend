package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.AddressDto;
import com.example.serbisyofullstack.dto.request.address.CreateAddressRequest;
import com.example.serbisyofullstack.dto.request.address.UpdateAddressRequest;
import com.example.serbisyofullstack.model.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Maps between {@link Address} and its DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper extends BaseMapper<Address, CreateAddressRequest, UpdateAddressRequest, AddressDto> {

    @Override
    @Mapping(source = "addressId", target = "id")
    AddressDto toDto(Address entity);

    @Override
    @Mapping(target = "addressId", ignore = true)
    @Mapping(target = "ownerId", ignore = true)   // taken from the authenticated principal
    Address toEntity(CreateAddressRequest request);

    @Override
    @Mapping(target = "addressId", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    Address toUpdate(UpdateAddressRequest request, @MappingTarget Address entity);
}
