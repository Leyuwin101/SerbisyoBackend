package com.example.serbisyofullstack.dto.request.notification;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for marking the current user's notifications as read. The
 * {@code readAt} timestamp is set by the server (see
 * {@link com.example.serbisyofullstack.model.entity.Notification#readAt});
 * the client only supplies the notification identifiers. An empty list marks
 * all of the user's notifications as read.
 */
@Getter
@Setter
@NoArgsConstructor
public class MarkNotificationReadRequest {

    @Schema(description = "IDs of the notifications to mark as read; empty/null means \"all\".")
    private List<Long> notificationIds;
}
