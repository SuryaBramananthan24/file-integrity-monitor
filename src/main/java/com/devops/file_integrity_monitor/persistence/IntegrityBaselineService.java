package com.devops.file_integrity_monitor.persistence;

import com.devops.file_integrity_monitor.integrity.IntegrityBaseline;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class IntegrityBaselineService {

    private final IntegrityBaselineRepository repository;
    public IntegrityBaselineService(IntegrityBaselineRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Optional<IntegrityBaseline> find(String sourceId,String resourceId) {
        return repository.findBySourceIdAndResourceId(sourceId, resourceId).map(this::toDomain);
    }

    @Transactional
    public IntegrityBaseline save(IntegrityBaseline baseline) {
        Instant now = Instant.now();
        IntegrityBaselineEntity entity = repository.findBySourceIdAndResourceId(baseline.sourceId(),baseline.resourceId()).orElse(null);
        if (entity == null) {
            entity = new IntegrityBaselineEntity(baseline.sourceId(),baseline.resourceId(),baseline.algorithm(),baseline.digest(),baseline.createdAt(),now);
        }
        else {
            entity.updateDigest(baseline.digest(), now);
        }

        IntegrityBaselineEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    private IntegrityBaseline toDomain(IntegrityBaselineEntity entity) {
        return new IntegrityBaseline(entity.getResourceId(),entity.getSourceId(),entity.getAlgorithm(),entity.getDigest(),entity.getCreatedAt());
    }
}