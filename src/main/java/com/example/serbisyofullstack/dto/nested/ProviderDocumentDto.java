package com.example.serbisyofullstack.dto.nested;

import com.example.serbisyofullstack.model.enums.DocumentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ProviderDocumentDto {

    private Long id;

    private Long providerId;

    private String type;

    private DocumentStatus status;

    private Long reviewedBy;

    private LocalDateTime reviewedAt;

    private LocalDateTime expiryDate;
}
