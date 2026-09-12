package com.example.serbisyofullstack.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * In-process Spring Cache. Only stable, low-sensitivity reference data is
 * cached:
 *
 * - "categories": service categories change rarely (admin-managed) and are read
 * on nearly every browse/search screen. Evicted on category create/update.
 *
 * Per the readme, user data, tokens, payments and anything user-scoped are
 * NEVER cached. When a second instance is introduced, replace this manager with
 * Redis (same cache names / keys).
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String CACHE_CATEGORIES = "categories";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CACHE_CATEGORIES);
    }
}
