package com.example.serbisyofullstack.audit;

/**
 * Records security-sensitive operations (admin actions, provider verification,
 * payment/refund changes, dispute resolutions, role changes) into the immutable
 * {@code AuditLog} trail.
 */
public interface AuditService {

    void record(Long actorUserId, String action, String entityType, Long entityId, String metadata);

    void record(Long actorUserId, String action, String entityRef);
}
