package com.example.serbisyofullstack.dto.response.messaging;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.ConversationSummaryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for opening a conversation. Returns either the newly created
 * conversation or the existing one between the same parties (deduplicated by
 * the service layer).
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateConversationResponse {

    @Schema(description = "The conversation (existing or newly created) in summary form.")
    private ConversationSummaryDto conversation;

    @Schema(description = "Server timestamp of creation.")
    private LocalDateTime createdAt;
}
