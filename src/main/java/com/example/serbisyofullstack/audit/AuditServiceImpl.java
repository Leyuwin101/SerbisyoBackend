package com.example.serbisyofullstack.audit;

import com.example.serbisyofullstack.model.entity.AuditLog;
import com.example.serbisyofullstack.model.entity.User;
import com.example.serbisyofullstack.repository.AuditLogRepository;
import com.example.serbisyofullstack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Persists audit entries. Audit writes must survive the caller's transaction
 * outcome, so each entry is committed in its own transaction (REQUIRES_NEW).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private static final Pattern ENTITY_REF = Pattern.compile("^(\\w+):(\\d+)$");

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void record(Long actorUserId, String action, String entityType, Long entityId, String metadata) {
        AuditLog entry = new AuditLog();
        if (actorUserId != null) {
            entry.setActor(userRepository.getReferenceById(actorUserId));
        }
        entry.setAction(action);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setMetadata(metadata);
        auditLogRepository.save(entry);
        log.info("AUDIT action={} entity={}:{} actor={}", action, entityType, entityId, actorUserId);
    }

    @Override
    public void record(Long actorUserId, String action, String entityRef) {
        Matcher m = ENTITY_REF.matcher(entityRef == null ? "" : entityRef);
        if (m.matches()) {
            record(actorUserId, action, m.group(1), Long.valueOf(m.group(2)), null);
        } else {
            record(actorUserId, action, "SYSTEM", null, entityRef);
        }
    }
}
