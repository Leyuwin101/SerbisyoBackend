package com.example.serbisyofullstack.dto.nested;

import com.example.serbisyofullstack.model.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NotificationDto {

    private Long id;

    private Long userId;

    private NotificationType type;

    private String title;

    private String body;

    private Boolean read;

    private LocalDateTime createdAt;

}
