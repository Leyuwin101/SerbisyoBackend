package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.ServiceSummaryDto;
import com.example.serbisyofullstack.dto.request.service.CreateServiceRequest;
import com.example.serbisyofullstack.dto.request.service.UpdateServiceRequest;
import com.example.serbisyofullstack.model.entity.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Maps between {@link Service} and {@link ServiceSummaryDto}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceMapper extends BaseMapper<Service, CreateServiceRequest, UpdateServiceRequest, ServiceSummaryDto> {

    @Override
    @Mapping(source = "serviceId", target = "id")
    @Mapping(source = "provider.providerProfileId", target = "providerId")
    @Mapping(source = "category.categoryId", target = "categoryId")
    ServiceSummaryDto toDto(Service entity);

    /** Public listing projection — same shape, keeps call sites explicit. */
    @Mapping(source = "serviceId", target = "id")
    @Mapping(source = "provider.providerProfileId", target = "providerId")
    @Mapping(source = "category.categoryId", target = "categoryId")
    ServiceSummaryDto toSummaryDto(Service entity);

    @Override
    @Mapping(target = "serviceId", ignore = true)
    @Mapping(target = "provider", ignore = true)   // from the authenticated provider
    @Mapping(target = "category", ignore = true)   // resolved from categoryId in the service
    Service toEntity(CreateServiceRequest request);

    @Override
    @Mapping(target = "serviceId", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "category", ignore = true)
    Service toUpdate(UpdateServiceRequest request, @MappingTarget Service entity);
}
