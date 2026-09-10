package com.example.serbisyofullstack.dto.response.messaging;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for marking messages as read. Returns the ids that were
 * actually marked plus the single {@code readAt} timestamp applied to all of
 * them, so the client can update unread badges.
 */
@Getter
@Setter
@NoArgsConstructor
public class MarkMessageReadResponse {

    @Schema(description = "Ids of the messages successfully marked as read.")
    private List<Long> messageIds;

    @Schema(description = "Timestamp applied to every marked message.")
    private LocalDateTime readAt;
}
