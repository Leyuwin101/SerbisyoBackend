package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.AvailabilityScheduleDto;
import com.example.serbisyofullstack.dto.request.availability.CreateAvailabilityScheduleRequest;
import com.example.serbisyofullstack.dto.request.availability.UpdateAvailabilityScheduleRequest;
import com.example.serbisyofullstack.model.entity.AvailabilitySchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Maps between {@link AvailabilitySchedule} and
 * {@link AvailabilityScheduleDto}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AvailabilityScheduleMapper extends BaseMapper<AvailabilitySchedule, CreateAvailabilityScheduleRequest, UpdateAvailabilityScheduleRequest, AvailabilityScheduleDto> {

    @Override
    @Mapping(source = "availabilityScheduleId", target = "id")
    @Mapping(source = "provider.providerProfileId", target = "providerId")
    @Mapping(source = "weekday", target = "dayOfWeek")
    AvailabilityScheduleDto toDto(AvailabilitySchedule entity);

    @Override
    @Mapping(target = "availabilityScheduleId", ignore = true)
    @Mapping(target = "provider", ignore = true)   // from the authenticated provider
    AvailabilitySchedule toEntity(CreateAvailabilityScheduleRequest request);

    @Override
    @Mapping(target = "availabilityScheduleId", ignore = true)
    @Mapping(target = "provider", ignore = true)
    AvailabilitySchedule toUpdate(UpdateAvailabilityScheduleRequest request, @MappingTarget AvailabilitySchedule entity);
}
