package com.example.serbisyofullstack.notification;

import com.example.serbisyofullstack.model.entity.Notification;
import com.example.serbisyofullstack.model.entity.User;
import com.example.serbisyofullstack.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates notifications: persists the Notification row, then fans the
 * message out to every enabled {@link NotificationChannel} (email, SMS, push).
 * Delivery failures on one channel never block the others.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationOrchestrator {

    private final NotificationRepository notificationRepository;
    private final List<NotificationChannel> channels;

    /**
     * Persist the notification and dispatch it over all enabled channels.
     */
    public void notifyUser(User user, String title, String body) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setBody(body);
        notification = notificationRepository.save(notification);

        dispatch(notification);
    }

    /**
     * Dispatch an already-persisted notification over all enabled channels.
     */
    public void dispatch(Notification notification) {
        for (NotificationChannel channel : channels) {
            if (!channel.isEnabled()) {
                log.debug("Skipping disabled channel '{}'", channel.getChannel());
                continue;
            }
            try {
                boolean accepted = channel.send(
                        resolveRecipient(channel, notification.getUser()),
                        notification.getTitle(),
                        notification.getBody());
                if (!accepted) {
                    log.warn("Channel '{}' did not accept notification {}", channel.getChannel(), notification.getNotificationId());
                }
            } catch (Exception ex) {
                // Never let one failing channel break the others or the caller.
                log.error("Channel '{}' failed to deliver notification {}: {}",
                        channel.getChannel(), notification.getNotificationId(), ex.getMessage(), ex);
            }
        }
    }

    private String resolveRecipient(NotificationChannel channel, User user) {
        return switch (channel.getChannel()) {
            case "email" ->
                user.getEmail();
            case "sms" ->
                user.getPhone() != null ? user.getPhone() : user.getEmail();
            default ->
                "user-" + user.getUserId();
        };
    }
}
