package com.example.serbisyofullstack.dto.response.notification;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for marking notifications as read. Reports how many
 * notifications were affected and the {@code readAt} timestamp applied.
 */
@Getter
@Setter
@NoArgsConstructor
public class MarkNotificationReadResponse {

    @Schema(description = "Number of notifications updated by this request.")
    private long updatedCount;

    @Schema(description = "Timestamp applied to every marked notification.")
    private LocalDateTime readAt;
}
