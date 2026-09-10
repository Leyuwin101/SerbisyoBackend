package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.DisputeDto;
import com.example.serbisyofullstack.dto.request.dispute.CreateDisputeRequest;
import com.example.serbisyofullstack.model.entity.Dispute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Maps between {@link Dispute} and {@link DisputeDto}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DisputeMapper {

    @Mapping(source = "disputeId", target = "id")
    @Mapping(source = "booking.bookingId", target = "bookingId")
    @Mapping(source = "openedBy.userId", target = "openedBy")
    @Mapping(source = "resolvedBy.userId", target = "resolvedBy")
    DisputeDto toDto(Dispute entity);

    @Mapping(target = "disputeId", ignore = true)
    @Mapping(target = "booking", ignore = true)    // resolved by the service
    @Mapping(target = "openedBy", ignore = true)   // from the authenticated principal
    @Mapping(target = "status", ignore = true)     // starts as OPEN
    @Mapping(target = "resolution", ignore = true)
    @Mapping(target = "resolvedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "resolvedAt", ignore = true)
    Dispute toEntity(CreateDisputeRequest request);
}
