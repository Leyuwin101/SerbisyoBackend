package com.example.serbisyofullstack.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Strongly typed application configuration. Every value comes from
 * application.properties / environment variables — never hardcoded.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

    private final Security security = new Security();
    private final Cors cors = new Cors();
    private final Storage storage = new Storage();
    private final Booking booking = new Booking();

    @Getter
    @Setter
    public static class Security {

        /**
         * HS256 secret; MUST come from an environment variable in production.
         */
        private String jwtSecret;
        private long jwtExpirationMinutes = 30;
        private long refreshTokenDays = 14;
    }

    @Getter
    @Setter
    public static class Cors {

        private String[] allowedOrigins = {"http://localhost:3000"};
        private String[] allowedMethods = {"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"};
    }

    @Getter
    @Setter
    public static class Storage {

        private String provider = "local";
        private String localRoot = "./uploads";
        private long maxFileBytes = 5 * 1024 * 1024;
    }

    @Getter
    @Setter
    public static class Booking {

        /**
         * Pending bookings older than this are expired by the scheduler.
         */
        private long pendingExpirationHours = 24;
    }
}
