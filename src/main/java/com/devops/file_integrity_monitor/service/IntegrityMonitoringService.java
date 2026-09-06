package com.devops.file_integrity_monitor.service;

import com.devops.file_integrity_monitor.integrity.*;
import com.devops.file_integrity_monitor.persistence.IntegrityBaselineService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

@Service
public class IntegrityMonitoringService {

    private static final String SOURCE_ID =
            "filesystem-local";

    private static final String HASH_ALGORITHM =
            "SHA-256";

    private final IntegrityBaselineService baselineService;
    private final IntegrityEvaluator integrityEvaluator;
    private final DigestService digestService;

    public IntegrityMonitoringService(
            IntegrityBaselineService baselineService,
            IntegrityEvaluator integrityEvaluator,
            DigestService digestService) {

        this.baselineService = baselineService;
        this.integrityEvaluator = integrityEvaluator;
        this.digestService = digestService;
    }

    /**
     * Create or update the PostgreSQL baseline
     * for the supplied file.
     */
    public IntegrityBaseline createBaseline(
            String filePath) throws IOException {

        Path path = normalizePath(filePath);

        String digest =
                digestService.calculate(path);

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

    /**
     * Retrieve the PostgreSQL baseline for a file.
     */
    public Optional<IntegrityBaseline> getBaseline(
            String filePath) {

        Path path = normalizePath(filePath);

        return baselineService.find(
                SOURCE_ID,
                path.toString()
        );
    }

    /**
     * Compare the current file contents against
     * the authoritative PostgreSQL baseline.
     */
    public IntegrityResult checkIntegrity(
            String filePath) {

        Path path = normalizePath(filePath);

        String resourceId =
                path.toString();

        Optional<IntegrityBaseline> baseline =
                baselineService.find(
                        SOURCE_ID,
                        resourceId
                );

        if (baseline.isEmpty()) {

            return new IntegrityResult(
                    resourceId,
                    IntegrityStatus.ERROR,
                    null,
                    null,
                    Instant.now()
            );
        }

        return integrityEvaluator.evaluate(
                path,
                baseline.get()
        );
    }

    private Path normalizePath(String filePath) {

        return Path.of(filePath)
                .toAbsolutePath()
                .normalize();
    }
}