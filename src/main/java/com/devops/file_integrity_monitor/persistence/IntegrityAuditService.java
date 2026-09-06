package com.devops.file_integrity_monitor.persistence;

import com.devops.file_integrity_monitor.audit.IntegrityAuditEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IntegrityAuditService {

    private final IntegrityAuditRepository repository;

    public IntegrityAuditService(IntegrityAuditRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public IntegrityAuditEvent save(IntegrityAuditEvent event) {

        IntegrityAuditEntity entity = new IntegrityAuditEntity(
                event.eventId(),
                event.sourceId(),
                event.resourceId(),
                event.eventType(),
                event.oldDigest(),
                event.newDigest(),
                event.occurredAt()
        );

        IntegrityAuditEntity saved = repository.save(entity);

        return toDomain(saved);
    }

    @Transactional(readOnly = true)
    public List<IntegrityAuditEvent> findByResourceId(String resourceId) {

        return repository
                .findByResourceIdOrderByOccurredAtDesc(resourceId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<IntegrityAuditEvent> findBySourceId(String sourceId) {

        return repository
                .findBySourceIdOrderByOccurredAtDesc(sourceId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private IntegrityAuditEvent toDomain(IntegrityAuditEntity entity) {

        return new IntegrityAuditEvent(
                entity.getEventId(),
                entity.getSourceId(),
                entity.getResourceId(),
                entity.getEventType(),
                entity.getOldDigest(),
                entity.getNewDigest(),
                entity.getOccurredAt()
        );
    }
}