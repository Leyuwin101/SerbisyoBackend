package com.example.serbisyofullstack.dto.request.messaging;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for marking the current user's messages as read. The
 * {@code readAt} timestamp itself is set by the server (see
 * {@link com.example.serbisyofullstack.model.entity.Message#readAt}), so the
 * client only supplies the message identifiers.
 */
@Getter
@Setter
@NoArgsConstructor
public class MarkMessageReadRequest {

    /**
     * IDs of the messages to mark as read; must belong to the caller's
     * conversations.
     */
    @NotNull(message = "At least one message id is required")
    private List<Long> messageIds;
}
