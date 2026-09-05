package com.devops.file_integrity_monitor.integrity;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

@Service
public class IntegrityEvaluator {
    private final DigestService digestService;
    public IntegrityEvaluator(DigestService digestService) {
        this.digestService = digestService;
    }

    public IntegrityResult evaluate(Path path,IntegrityBaseline baseline) {
        String resourceId = path.toAbsolutePath().normalize().toString();
        Instant checkedAt = Instant.now();
        if (!Files.exists(path)) {
            return new IntegrityResult(resourceId,IntegrityStatus.UNAVAILABLE,baseline.digest(),null,checkedAt);
        }
        try {
            String currentDigest = digestService.calculate(path);
            IntegrityStatus status = baseline.digest().equalsIgnoreCase(currentDigest)? IntegrityStatus.UNCHANGED : IntegrityStatus.CHANGED;
            return new IntegrityResult(resourceId,status,baseline.digest(),currentDigest,checkedAt);
        } catch (IOException exception) {
            return new IntegrityResult(resourceId,IntegrityStatus.ERROR,baseline.digest(),null,checkedAt);
        }
    }
}