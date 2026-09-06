package com.devops.file_integrity_monitor.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "integrity_audit_events",
        indexes = {
                @Index(
                        name = "idx_audit_resource_time",
                        columnList = "resource_id, occurred_at"
                ),
                @Index(
                        name = "idx_audit_source_time",
                        columnList = "source_id, occurred_at"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_audit_event_id",
                        columnNames = "event_id"
                )
        }
)
public class IntegrityAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, length = 100)
    private String eventId;

    @Column(name = "source_id", nullable = false, length = 255)
    private String sourceId;

    @Column(name = "resource_id", nullable = false, length = 1000)
    private String resourceId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "old_digest", length = 128)
    private String oldDigest;

    @Column(name = "new_digest", length = 128)
    private String newDigest;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected IntegrityAuditEntity() {
    }

    public IntegrityAuditEntity(
            String eventId,
            String sourceId,
            String resourceId,
            String eventType,
            String oldDigest,
            String newDigest,
            Instant occurredAt
    ) {
        this.eventId = eventId;
        this.sourceId = sourceId;
        this.resourceId = resourceId;
        this.eventType = eventType;
        this.oldDigest = oldDigest;
        this.newDigest = newDigest;
        this.occurredAt = occurredAt;
    }

    public Long getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getOldDigest() {
        return oldDigest;
    }

    public String getNewDigest() {
        return newDigest;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}