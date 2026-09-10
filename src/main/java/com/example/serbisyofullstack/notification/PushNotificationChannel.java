package com.example.serbisyofullstack.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Push notification delivery channel. Logs the notification for now; wire a
 * real push provider (e.g. FCM, APNs) behind this adapter later.
 */
@Slf4j
@Component
public class PushNotificationChannel implements NotificationChannel {

    @Override
    public String getChannel() {
        return "push";
    }

    @Override
    public boolean isEnabled() {
        // Disabled until a provider is configured.
        return false;
    }

    @Override
    public boolean send(String recipient, String title, String body) {
        log.info("[PUSH] to={} title='{}' body='{}'", recipient, title, body);
        return true;
    }
}
