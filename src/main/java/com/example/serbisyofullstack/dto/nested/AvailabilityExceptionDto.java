package com.example.serbisyofullstack.dto.nested;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AvailabilityExceptionDto {

    private Long id;

    private Long providerId;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private Boolean available;

    private String reason;
}
