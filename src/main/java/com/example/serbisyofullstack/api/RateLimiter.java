package com.example.serbisyofullstack.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

/**
 * Fixed-window in-memory rate limiter for sensitive endpoints (auth, search,
 * messaging, reports). Limits are per client key (user id or IP) per window.
 *
 * This is intentionally simple and single-node; when the app is deployed behind
 * multiple instances, move to Redis-backed rate limiting. Buckets are pruned
 * lazily so the map cannot grow unbounded.
 */
@Component
public class RateLimiter {

    private record Bucket(long windowStart, AtomicLong count) {

    }

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Try to consume one slot for the given key.
     *
     * @return true when the request is allowed
     */
    public boolean tryAcquire(String key, int limit, long windowMillis) {
        long now = System.currentTimeMillis();
        Bucket bucket = buckets.compute(key, (k, existing) -> {
            if (existing == null || now - existing.windowStart() >= windowMillis) {
                return new Bucket(now, new AtomicLong(0));
            }
            return existing;
        });
        if (buckets.size() > 10_000) {
            buckets.entrySet().removeIf(e -> now - e.getValue().windowStart() >= windowMillis);
        }
        return bucket.count().incrementAndGet() <= limit;
    }
}
