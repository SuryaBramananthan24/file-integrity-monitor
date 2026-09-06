package com.devops.file_integrity_monitor.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IntegrityAuditRepository
        extends JpaRepository<IntegrityAuditEntity, Long> {

    Optional<IntegrityAuditEntity> findByEventId(String eventId);

    List<IntegrityAuditEntity> findByResourceIdOrderByOccurredAtDesc(
            String resourceId
    );

    List<IntegrityAuditEntity> findBySourceIdOrderByOccurredAtDesc(
            String sourceId
    );
}