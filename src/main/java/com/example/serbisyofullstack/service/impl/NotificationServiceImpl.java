package com.example.serbisyofullstack.service.impl;

import com.example.serbisyofullstack.dto.nested.NotificationDto;
import com.example.serbisyofullstack.dto.request.notification.MarkNotificationReadRequest;
import com.example.serbisyofullstack.dto.response.notification.MarkNotificationReadResponse;
import com.example.serbisyofullstack.exception.ResourceNotFoundException;
import com.example.serbisyofullstack.mapper.NotificationMapper;
import com.example.serbisyofullstack.model.entity.Notification;
import com.example.serbisyofullstack.model.entity.User;
import com.example.serbisyofullstack.model.enums.NotificationType;
import com.example.serbisyofullstack.notification.NotificationOrchestrator;
import com.example.serbisyofullstack.repository.NotificationRepository;
import com.example.serbisyofullstack.repository.UserRepository;
import com.example.serbisyofullstack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Notification querying + read-state for the authenticated user. Every query is
 * scoped to the current user id so a user can never read or mark another user's
 * notifications. Outbound delivery is delegated to the notification package
 * orchestrator; this class never talks to email/SMS providers.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationOrchestrator notificationOrchestrator;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> listNotifications(Long currentUserId, Pageable pageable) {
        return notificationRepository.findByUserId(currentUserId, pageable).map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> listUnread(Long currentUserId, Pageable pageable) {
        return notificationRepository.findByUserIdAndReadAtIsNull(currentUserId, pageable)
                .map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(Long currentUserId) {
        return notificationRepository.countByUserIdAndReadAtIsNull(currentUserId);
    }

    @Override
    @Transactional
    public MarkNotificationReadResponse markRead(Long currentUserId, MarkNotificationReadRequest request) {
        List<Long> ids = request == null ? null : request.getNotificationIds();
        List<Notification> notifications = (ids == null || ids.isEmpty())
                ? notificationRepository.findByUserIdAndReadAtIsNull(currentUserId)
                : notificationRepository.findAllById(ids).stream()
                        .filter(n -> n.getUser().getUserId().equals(currentUserId) && n.getReadAt() == null)
                        .toList();
        LocalDateTime readAt = LocalDateTime.now();
        notifications.forEach(n -> n.setReadAt(readAt));
        notificationRepository.saveAll(notifications);
        MarkNotificationReadResponse response = new MarkNotificationReadResponse();
        response.setUpdatedCount(notifications.size());
        response.setReadAt(readAt);
        return response;
    }

    @Override
    @Transactional
    public void markAllRead(Long currentUserId) {
        List<Notification> unread = notificationRepository.findByUserIdAndReadAtIsNull(currentUserId);
        LocalDateTime readAt = LocalDateTime.now();
        unread.forEach(n -> n.setReadAt(readAt));
        notificationRepository.saveAll(unread);
    }

    @Override
    @Transactional
    public void notifyUser(Long userId, String title, String body) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        notificationOrchestrator.notifyUser(user, title, body);
        log.debug("Notification dispatched to user {}: {}", userId, title);
    }
}
