package com.example.serbisyofullstack.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Email delivery channel. Logs the notification for now; wire a real mail
 * sender (e.g. JavaMailSender, SES, SendGrid) behind this adapter later.
 */
@Slf4j
@Component
public class EmailNotificationChannel implements NotificationChannel {

    @Override
    public String getChannel() {
        return "email";
    }

    @Override
    public boolean isEnabled() {
        // Enabled by default; flip based on configuration when a real sender is wired.
        return true;
    }

    @Override
    public boolean send(String recipient, String title, String body) {
        log.info("[EMAIL] to={} subject='{}' body='{}'", recipient, title, body);
        return true;
    }
}
