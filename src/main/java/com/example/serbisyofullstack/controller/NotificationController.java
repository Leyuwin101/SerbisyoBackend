package com.example.serbisyofullstack.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.serbisyofullstack.api.PaginationGuard;
import com.example.serbisyofullstack.dto.nested.NotificationDto;
import com.example.serbisyofullstack.dto.request.notification.MarkNotificationReadRequest;
import com.example.serbisyofullstack.dto.response.notification.MarkNotificationReadResponse;
import com.example.serbisyofullstack.security.CurrentUserService;
import com.example.serbisyofullstack.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Notifications for the authenticated user. Every query is scoped to the
 * current user id — clients can never read someone else's notifications.
 */
@Tag(name = "Notifications", description = "In-app notifications of the authenticated user")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    @Operation(summary = "List the user's notifications")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notifications retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping
    public ResponseEntity<Page<NotificationDto>> listNotifications(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(notificationService.listNotifications(
                currentUserService.getCurrentUserId(), PaginationGuard.cap(pageable)));
    }

    @Operation(summary = "List the user's unread notifications")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Unread notifications retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/unread")
    public ResponseEntity<Page<NotificationDto>> listUnread(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(notificationService.listUnread(
                currentUserService.getCurrentUserId(), PaginationGuard.cap(pageable)));
    }

    @Operation(summary = "Count the user's unread notifications")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Count retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/unread/count")
    public ResponseEntity<Long> countUnread() {
        return ResponseEntity.ok(notificationService.countUnread(
                currentUserService.getCurrentUserId()));
    }

    @Operation(summary = "Mark specific notifications as read")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notifications marked read"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping("/read")
    public ResponseEntity<MarkNotificationReadResponse> markRead(
            @Valid @RequestBody MarkNotificationReadRequest request) {
        return ResponseEntity.ok(notificationService.markRead(
                currentUserService.getCurrentUserId(), request));
    }

    @Operation(summary = "Mark all notifications as read")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "All notifications marked read"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllRead() {
        notificationService.markAllRead(currentUserService.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}
