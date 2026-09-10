package com.example.serbisyofullstack.dto.response.messaging;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.MessageDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for sending a message. The nested {@link MessageDto} carries
 * the persisted message including its server-assigned id and {@code sentAt}
 * timestamp.
 */
@Getter
@Setter
@NoArgsConstructor
public class SendMessageResponse {

    @Schema(description = "The saved message, ready to be appended to the local thread.")
    private MessageDto message;

    @Schema(description = "Server timestamp of when the message was sent.")
    private LocalDateTime sentAt;
}
