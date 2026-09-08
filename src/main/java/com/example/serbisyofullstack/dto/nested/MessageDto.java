package com.example.serbisyofullstack.dto.nested;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MessageDto {

    private Long id;

    private Long conversationId;

    private Long senderId;

    private String content;

    private LocalDateTime readAt;

    private String attachmentReference;
}
