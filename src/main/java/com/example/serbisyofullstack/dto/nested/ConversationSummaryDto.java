package com.example.serbisyofullstack.dto.nested;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ConversationSummaryDto {

    private Long id;

    private Long customerId;

    private Long providerId;

    private Long bookingId;

    private String lastMessage;

    private LocalDateTime lastMessageAt;
}
