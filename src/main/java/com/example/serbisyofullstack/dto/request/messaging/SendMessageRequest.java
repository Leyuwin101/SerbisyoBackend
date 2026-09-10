package com.example.serbisyofullstack.dto.request.messaging;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for sending a message inside an existing conversation.
 * {@code sender} and {@code sentAt} are set by the server from the
 * authenticated principal and current time.
 */
@Getter
@Setter
@NoArgsConstructor
public class SendMessageRequest {
    @Schema(description = "The message text, up to 5000 characters.")
    @NotBlank(message = "Message content is required")
    @Size(max = 5000, message = "Message must not exceed 5000 characters")
    private String content;

    @Schema(description = "Optional storage key of an uploaded file attached to the message.")
    @Size(max = 500)
    private String attachmentReference;
}
