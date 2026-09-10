package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.ServiceImageDto;
import com.example.serbisyofullstack.dto.request.service.ServiceImageRequest;
import com.example.serbisyofullstack.model.entity.ServiceImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Maps between {@link ServiceImage} and {@link ServiceImageDto}. Images are
 * replaced wholesale on service update, so no toUpdate mapping is needed.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceImageMapper {

    @Mapping(source = "serviceImageId", target = "id")
    @Mapping(source = "service.serviceId", target = "serviceId")
    ServiceImageDto toDto(ServiceImage entity);

    List<ServiceImageDto> toDtoList(List<ServiceImage> entities);

    @Mapping(target = "serviceImageId", ignore = true)
    @Mapping(target = "service", ignore = true)
    ServiceImage toEntity(ServiceImageRequest request);

    List<ServiceImage> toEntityList(List<ServiceImageRequest> requests);
}
