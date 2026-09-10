package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.ProviderDocumentDto;
import com.example.serbisyofullstack.dto.request.provider.CreateProviderDocumentRequest;
import com.example.serbisyofullstack.model.entity.ProviderDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Maps between {@link ProviderDocument} and {@link ProviderDocumentDto}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProviderDocumentMapper {

    @Mapping(source = "documentId", target = "id")
    @Mapping(source = "provider.providerProfileId", target = "providerId")
    @Mapping(source = "reviewedBy.userId", target = "reviewedBy")
    @Mapping(target = "reviewedAt", source = "reviewedAt")
    @Mapping(target = "expiryDate", expression = "java(toDateTime(entity.getExpiryDate()))")
    ProviderDocumentDto toDto(ProviderDocument entity);

    @Mapping(target = "documentId", ignore = true)
    @Mapping(target = "provider", ignore = true)     // from the authenticated provider
    @Mapping(target = "status", ignore = true)       // starts as PENDING
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    ProviderDocument toEntity(CreateProviderDocumentRequest request);

    default LocalDateTime toDateTime(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }
}
