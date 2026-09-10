package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.AuditLogDto;
import com.example.serbisyofullstack.model.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Maps {@link AuditLog} to {@link AuditLogDto}. Audit entries are written by
 * the audit aspect, so only entity-to-DTO mapping exists.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuditLogMapper {

    @Mapping(source = "auditLogId", target = "id")
    @Mapping(source = "actor.userId", target = "actorId")
    @Mapping(target = "metadata", source = "metadata", qualifiedByName = "jsonToMap")
    AuditLogDto toDto(AuditLog entity);

    @Named("jsonToMap")
    default Map<String, Object> jsonToMap(String json) {
        if (json == null || json.isBlank()) {
            return java.util.Map.of();
        }
        try {
            return new ObjectMapper().readValue(json, new TypeReference<>() {
            });
        } catch (Exception ex) {
            return java.util.Map.of();
        }
    }
}
