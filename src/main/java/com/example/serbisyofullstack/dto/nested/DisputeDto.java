package com.example.serbisyofullstack.dto.nested;

import com.example.serbisyofullstack.model.enums.DisputeStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class DisputeDto {

    private Long id;

    private Long bookingId;

    private Long openedBy;

    private String reason;

    private DisputeStatus status;

    private String resolution;

    private Long resolvedBy;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

}
