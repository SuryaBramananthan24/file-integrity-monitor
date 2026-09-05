package com.devops.file_integrity_monitor.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IntegrityBaselineRepository extends JpaRepository<IntegrityBaselineEntity, Long> {
    Optional<IntegrityBaselineEntity> findBySourceIdAndResourceId(String sourceId,String resourceId);
}