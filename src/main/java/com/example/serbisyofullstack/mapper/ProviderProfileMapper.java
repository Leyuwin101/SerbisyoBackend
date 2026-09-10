package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.ProviderSummaryDto;
import com.example.serbisyofullstack.dto.request.provider.CreateProviderProfileRequest;
import com.example.serbisyofullstack.dto.request.provider.UpdateProviderProfileRequest;
import com.example.serbisyofullstack.model.entity.ProviderProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Maps between {@link ProviderProfile} and {@link ProviderSummaryDto}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProviderProfileMapper extends BaseMapper<ProviderProfile, CreateProviderProfileRequest, UpdateProviderProfileRequest, ProviderSummaryDto> {

    @Override
    @Mapping(source = "providerProfileId", target = "id")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(target = "verificationStatus", expression = "java(entity.getVerificationStatus() != null ? entity.getVerificationStatus().name() : null)")
    ProviderSummaryDto toDto(ProviderProfile entity);

    @Override
    @Mapping(target = "providerProfileId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "verificationStatus", ignore = true)   // starts as PENDING
    @Mapping(target = "averageRating", ignore = true)        // computed, not client-supplied
    @Mapping(target = "reviewCount", ignore = true)
    ProviderProfile toEntity(CreateProviderProfileRequest request);

    @Override
    @Mapping(target = "providerProfileId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "verificationStatus", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    ProviderProfile toUpdate(UpdateProviderProfileRequest request, @MappingTarget ProviderProfile entity);
}
