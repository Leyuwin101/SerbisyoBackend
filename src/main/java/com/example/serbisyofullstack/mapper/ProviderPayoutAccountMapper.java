package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.ProviderPayoutAccountDto;
import com.example.serbisyofullstack.dto.request.provider.CreateProviderPayoutAccountRequest;
import com.example.serbisyofullstack.dto.request.provider.UpdateProviderPayoutAccountRequest;
import com.example.serbisyofullstack.model.entity.ProviderPayoutAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Maps between {@link ProviderPayoutAccount} and
 * {@link ProviderPayoutAccountDto}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProviderPayoutAccountMapper extends BaseMapper<ProviderPayoutAccount, CreateProviderPayoutAccountRequest, UpdateProviderPayoutAccountRequest, ProviderPayoutAccountDto> {

    @Override
    @Mapping(source = "payoutAccountId", target = "id")
    @Mapping(source = "provider.providerProfileId", target = "providerId")
    @Mapping(source = "gatewayAccountReference", target = "providerReference")
    @Mapping(target = "readyForPayout", expression = "java(entity.getStatus() == com.example.serbisyofullstack.model.enums.PayoutAccountStatus.READY)")
    ProviderPayoutAccountDto toDto(ProviderPayoutAccount entity);

    @Override
    @Mapping(target = "payoutAccountId", ignore = true)
    @Mapping(target = "provider", ignore = true)   // from the authenticated provider
    @Mapping(target = "status", ignore = true)     // starts as PENDING
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProviderPayoutAccount toEntity(CreateProviderPayoutAccountRequest request);

    @Override
    @Mapping(target = "payoutAccountId", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProviderPayoutAccount toUpdate(UpdateProviderPayoutAccountRequest request, @MappingTarget ProviderPayoutAccount entity);
}
