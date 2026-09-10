package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.ReportSummaryDto;
import com.example.serbisyofullstack.dto.request.report.CreateReportRequest;
import com.example.serbisyofullstack.model.entity.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Maps between {@link Report} and {@link ReportSummaryDto}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReportMapper {

    @Mapping(source = "reportId", target = "id")
    @Mapping(source = "reportedBy.userId", target = "reporterId")
    ReportSummaryDto toDto(Report entity);

    @Mapping(target = "reportId", ignore = true)
    @Mapping(target = "reportedBy", ignore = true)   // from the authenticated principal
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "status", ignore = true)       // starts as OPEN
    @Mapping(target = "resolution", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "resolvedAt", ignore = true)
    Report toEntity(CreateReportRequest request);
}
