package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.AvailabilityExceptionDto;
import com.example.serbisyofullstack.dto.request.availability.CreateAvailabilityExceptionRequest;
import com.example.serbisyofullstack.dto.request.availability.UpdateAvailabilityExceptionRequest;
import com.example.serbisyofullstack.model.entity.AvailabilityException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Maps between {@link AvailabilityException} and
 * {@link AvailabilityExceptionDto}. The DTO exposes a single start/end
 * datetime; the entity stores a date plus optional times, so the conversion is
 * done in default methods.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AvailabilityExceptionMapper extends BaseMapper<AvailabilityException, CreateAvailabilityExceptionRequest, UpdateAvailabilityExceptionRequest, AvailabilityExceptionDto> {

    @Override
    @Mapping(source = "availabilityExceptionId", target = "id")
    @Mapping(source = "provider.providerProfileId", target = "providerId")
    @Mapping(target = "startDateTime", expression = "java(toStartDateTime(entity))")
    @Mapping(target = "endDateTime", expression = "java(toEndDateTime(entity))")
    AvailabilityExceptionDto toDto(AvailabilityException entity);

    @Override
    @Mapping(target = "availabilityExceptionId", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "exceptionDate", source = "exceptionDate")
    AvailabilityException toEntity(CreateAvailabilityExceptionRequest request);

    @Override
    @Mapping(target = "availabilityExceptionId", ignore = true)
    @Mapping(target = "provider", ignore = true)
    AvailabilityException toUpdate(UpdateAvailabilityExceptionRequest request, @MappingTarget AvailabilityException entity);

    default LocalDateTime toStartDateTime(AvailabilityException entity) {
        LocalTime start = entity.getStartTime() != null ? entity.getStartTime() : LocalTime.MIDNIGHT;
        return LocalDateTime.of(entity.getExceptionDate(), start);
    }

    default LocalDateTime toEndDateTime(AvailabilityException entity) {
        LocalTime end = entity.getEndTime() != null ? entity.getEndTime() : LocalTime.MAX;
        return LocalDateTime.of(entity.getExceptionDate(), end);
    }
}
