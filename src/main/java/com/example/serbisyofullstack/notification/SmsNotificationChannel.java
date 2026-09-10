package com.example.serbisyofullstack.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * SMS delivery channel. Logs the notification for now; wire a real SMS provider
 * (e.g. Twilio, Semaphore) behind this adapter later.
 */
@Slf4j
@Component
public class SmsNotificationChannel implements NotificationChannel {

    @Override
    public String getChannel() {
        return "sms";
    }

    @Override
    public boolean isEnabled() {
        // Disabled until a provider is configured.
        return false;
    }

    @Override
    public boolean send(String recipient, String title, String body) {
        log.info("[SMS] to={} body='{}'", recipient, body);
        return true;
    }
}
