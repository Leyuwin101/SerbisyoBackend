package com.example.serbisyofullstack.notification;

/**
 * Abstraction for a single notification delivery channel (email, SMS, push).
 * Implementations decide whether a given channel is enabled/configured and how
 * the payload is delivered.
 */
public interface NotificationChannel {

    /**
     * @return the channel identifier, e.g. "email", "sms", "push".
     */
    String getChannel();

    /**
     * @return true when the channel is configured and able to send.
     */
    boolean isEnabled();

    /**
     * Deliver a notification to the given recipient over this channel.
     *
     * @param recipient the user's contact identifier for the channel (email
     * address, phone number, device token, ...)
     * @param title notification title/subject
     * @param body notification body
     * @return true when the channel accepted the message
     */
    boolean send(String recipient, String title, String body);
}
