package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.CustomerSummaryDto;
import com.example.serbisyofullstack.dto.request.user.UpdateCustomerProfileRequest;
import com.example.serbisyofullstack.model.entity.CustomerProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Maps {@link CustomerProfile} to {@link CustomerSummaryDto}. Profiles are
 * created together with the owning User, so only update mapping is needed.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerProfileMapper extends BaseMapper<CustomerProfile, Void, UpdateCustomerProfileRequest, CustomerSummaryDto> {

    @Override
    @Mapping(source = "customerProfileId", target = "id")
    @Mapping(source = "user.userId", target = "userId")
    CustomerSummaryDto toDto(CustomerProfile entity);

    @Override
    default CustomerProfile toEntity(Void ignored) {
        throw new UnsupportedOperationException("Customer profiles are created with their User");
    }

    @Override
    @Mapping(target = "customerProfileId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "defaultAddress", ignore = true)
    void toUpdate(UpdateCustomerProfileRequest request, @MappingTarget CustomerProfile entity);
}
