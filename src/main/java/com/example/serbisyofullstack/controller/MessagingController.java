package com.example.serbisyofullstack.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.serbisyofullstack.api.PaginationGuard;
import com.example.serbisyofullstack.api.RateLimiter;
import com.example.serbisyofullstack.dto.nested.ConversationSummaryDto;
import com.example.serbisyofullstack.dto.nested.MessageDto;
import com.example.serbisyofullstack.dto.request.messaging.CreateConversationRequest;
import com.example.serbisyofullstack.dto.request.messaging.MarkMessageReadRequest;
import com.example.serbisyofullstack.dto.request.messaging.SendMessageRequest;
import com.example.serbisyofullstack.dto.response.messaging.CreateConversationResponse;
import com.example.serbisyofullstack.dto.response.messaging.MarkMessageReadResponse;
import com.example.serbisyofullstack.dto.response.messaging.SendMessageResponse;
import com.example.serbisyofullstack.exception.TooManyRequestsException;
import com.example.serbisyofullstack.security.CurrentUserService;
import com.example.serbisyofullstack.service.MessagingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Messaging endpoints. Participation in a conversation is verified in
 * {@link com.example.serbisyofullstack.service.impl.MessagingServiceImpl};
 * sending messages is rate limited per user.
 */
@Tag(name = "Messaging", description = "Conversations and messages between customers and providers")
@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class MessagingController {

    private final MessagingService messagingService;
    private final CurrentUserService currentUserService;
    private final RateLimiter rateLimiter;

    @Operation(summary = "Open a conversation with a provider")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Conversation created (or existing one returned)"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "404", description = "Provider or booking not found")
    })
    @PostMapping
    public ResponseEntity<CreateConversationResponse> createConversation(
            @Valid @RequestBody CreateConversationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messagingService.createConversation(
                        currentUserService.getCurrentUserId(), request));
    }

    @Operation(summary = "List the user's conversations")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Conversations retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping
    public ResponseEntity<Page<ConversationSummaryDto>> listConversations(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(messagingService.listConversations(
                currentUserService.getCurrentUserId(), PaginationGuard.cap(pageable)));
    }

    @Operation(summary = "List messages in a conversation")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Messages retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not a participant of the conversation"),
        @ApiResponse(responseCode = "404", description = "Conversation not found")
    })
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<Page<MessageDto>> listMessages(
            @PathVariable Long conversationId,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(messagingService.listMessages(
                currentUserService.getCurrentUserId(), conversationId, PaginationGuard.cap(pageable)));
    }

    @Operation(summary = "Send a message in a conversation")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Message sent"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not a participant of the conversation"),
        @ApiResponse(responseCode = "429", description = "Sending messages too fast")
    })
    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<SendMessageResponse> sendMessage(
            @PathVariable Long conversationId,
            @Valid @RequestBody SendMessageRequest request,
            HttpServletRequest httpRequest) {
        Long userId = currentUserService.getCurrentUserId();
        if (!rateLimiter.tryAcquire("messages:send:" + userId, 60, 60_000)) {
            throw new TooManyRequestsException("Sending messages too fast, please slow down");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messagingService.sendMessage(userId, conversationId, request));
    }

    @Operation(summary = "Mark a conversation's messages as read")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Messages marked read"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not a participant of the conversation")
    })
    @PostMapping("/messages/read")
    public ResponseEntity<MarkMessageReadResponse> markMessagesRead(
            @Valid @RequestBody MarkMessageReadRequest request) {
        return ResponseEntity.ok(messagingService.markMessageRead(
                currentUserService.getCurrentUserId(), request));
    }
}
