package com.example.serbisyofullstack.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.cfg.DateTimeFeature;

/**
 * Timestamps are serialized/deserialized as ISO-8601 strings (UTC), never as
 * numeric timestamps. Entities keep UTC {@code LocalDateTime}; timezone
 * conversion happens at the application edge. Spring Boot 4 ships Jackson 3
 * (tools.jackson), which integrates java-time support natively — only the
 * timestamp-writing feature needs to be pinned off explicitly.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public JsonMapperBuilderCustomizer jacksonUtcCustomizer() {
        return builder -> builder.disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
