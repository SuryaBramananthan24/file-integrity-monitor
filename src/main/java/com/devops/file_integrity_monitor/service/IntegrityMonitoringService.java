package com.devops.file_integrity_monitor.service;

import com.devops.file_integrity_monitor.audit.IntegrityAuditEvent;
import com.devops.file_integrity_monitor.integrity.*;
import com.devops.file_integrity_monitor.persistence.IntegrityAuditService;
import com.devops.file_integrity_monitor.persistence.IntegrityBaselineService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class IntegrityMonitoringService {

    private static final String SOURCE_ID = "filesystem-local";
    private static final String HASH_ALGORITHM = "SHA-256";

    private final IntegrityBaselineService baselineService;
    private final IntegrityAuditService auditService;
    private final IntegrityEvaluator integrityEvaluator;
    private final DigestService digestService;

    public IntegrityMonitoringService(
            IntegrityBaselineService baselineService,
            IntegrityAuditService auditService,
            IntegrityEvaluator integrityEvaluator,
            DigestService digestService
    ) {
        this.baselineService = baselineService;
        this.auditService = auditService;
        this.integrityEvaluator = integrityEvaluator;
        this.digestService = digestService;
    }

    public IntegrityResult checkIntegrity(String filePath) {

        Path path = normalizePath(filePath);
        String resourceId = path.toString();

        Optional<IntegrityBaseline> baseline =
                baselineService.find(SOURCE_ID, resourceId);

        if (baseline.isEmpty()) {

            IntegrityResult result = new IntegrityResult(
                    resourceId,
                    IntegrityStatus.ERROR,
                    null,
                    null,
                    Instant.now()
            );

            persistAudit(
                    resourceId,
                    IntegrityStatus.ERROR,
                    null,
                    null
            );

            return result;
        }

        IntegrityResult result =
                integrityEvaluator.evaluate(path, baseline.get());

        persistAudit(
                resourceId,
                result.status(),
                baseline.get().digest(),
                result.currentDigest()
        );

        return result;
    }

    public IntegrityBaseline createBaseline(String filePath)
            throws IOException {

        Path path = normalizePath(filePath);

        String digest = digestService.calculate(path);

        IntegrityBaseline baseline =
                new IntegrityBaseline(
                        path.toString(),
                        SOURCE_ID,
                        HASH_ALGORITHM,
                        digest,
                        Instant.now()
                );

        return baselineService.save(baseline);
    }

    private void persistAudit(
            String resourceId,
            IntegrityStatus status,
            String oldDigest,
            String newDigest
    ) {

        IntegrityAuditEvent event =
                new IntegrityAuditEvent(
                        UUID.randomUUID().toString(),
                        SOURCE_ID,
                        resourceId,
                        status.name(),
                        oldDigest,
                        newDigest,
                        Instant.now()
                );

        auditService.save(event);
    }

    private Path normalizePath(String filePath) {

        return Path.of(filePath)
                .toAbsolutePath()
                .normalize();
    }

    public Optional<IntegrityBaseline> getBaseline(String filePath) {
        String resourceId = Path.of(filePath)
                .toAbsolutePath()
                .normalize()
                .toString();

        return baselineService.find(SOURCE_ID, resourceId);
    }
}