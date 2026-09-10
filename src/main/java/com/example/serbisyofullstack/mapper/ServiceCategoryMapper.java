package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.ServiceCategoryDto;
import com.example.serbisyofullstack.dto.request.category.CreateServiceCategoryRequest;
import com.example.serbisyofullstack.dto.request.category.UpdateServiceCategoryRequest;
import com.example.serbisyofullstack.model.entity.ServiceCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Maps between {@link ServiceCategory} and {@link ServiceCategoryDto}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceCategoryMapper extends BaseMapper<ServiceCategory, CreateServiceCategoryRequest, UpdateServiceCategoryRequest, ServiceCategoryDto> {

    @Override
    @Mapping(source = "categoryId", target = "id")
    ServiceCategoryDto toDto(ServiceCategory entity);

    @Override
    @Mapping(target = "categoryId", ignore = true)
    ServiceCategory toEntity(CreateServiceCategoryRequest request);

    @Override
    @Mapping(target = "categoryId", ignore = true)
    ServiceCategory toUpdate(UpdateServiceCategoryRequest request, @MappingTarget ServiceCategory entity);
}
