package com.example.serbisyofullstack.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_actor", columnList = "actor_id"),
                @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
                @Index(name = "idx_audit_timpestamp", columnList = "timestamp")
        }
)
public class AuditLog {
}
