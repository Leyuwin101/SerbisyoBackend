package com.example.serbisyofullstack.dto.nested;

import com.example.serbisyofullstack.model.enums.ReportStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ReportSummaryDto {

    private Long id;

    private Long reporterId;

    private String entityType;

    private Long entityId;

    private String reason;

    private ReportStatus status;

    private LocalDateTime createdAt;
}
