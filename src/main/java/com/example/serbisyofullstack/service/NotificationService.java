package com.example.serbisyofullstack.service;

import com.example.serbisyofullstack.dto.request.notification.MarkNotificationReadRequest;
import com.example.serbisyofullstack.dto.nested.NotificationDto;
import com.example.serbisyofullstack.dto.response.notification.MarkNotificationReadResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Notification querying + read-state for the authenticated user. Delivery
 * fan-out lives in the notification package (orchestrator + channels).
 */
public interface NotificationService {

    Page<NotificationDto> listNotifications(Long currentUserId, Pageable pageable);

    Page<NotificationDto> listUnread(Long currentUserId, Pageable pageable);

    long countUnread(Long currentUserId);

    MarkNotificationReadResponse markRead(Long currentUserId, MarkNotificationReadRequest request);

    void markAllRead(Long currentUserId);

    /**
     * Business-event notification entry point used by other services
     * (booking lifecycle, disputes, payments...). Delivery is delegated to
     * the notification package orchestrator/channels.
     */
    void notifyUser(Long userId, String title, String body);
}
