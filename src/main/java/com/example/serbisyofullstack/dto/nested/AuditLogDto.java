package com.example.serbisyofullstack.dto.nested;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
public class AuditLogDto {

    private Long id;

    private Long actorId;

    private String action;

    private String entityType;

    private Long entityId;

    private Map<String, Object> metadata;

    private LocalDateTime timestamp;
}
