package com.devops.file_integrity_monitor.service;

import com.devops.file_integrity_monitor.integrity.IntegrityBaseline;
import com.devops.file_integrity_monitor.integrity.IntegrityEvaluator;
import com.devops.file_integrity_monitor.integrity.IntegrityResult;
import com.devops.file_integrity_monitor.integrity.IntegrityStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;

@Service
public class IntegrityMonitoringService {

    private final BaselineService baselineService;
    private final IntegrityEvaluator integrityEvaluator;

    public IntegrityMonitoringService(
            BaselineService baselineService,
            IntegrityEvaluator integrityEvaluator) {

        this.baselineService = baselineService;
        this.integrityEvaluator = integrityEvaluator;
    }

    public IntegrityResult checkIntegrity(String filePath) {

        String resourceId = Path.of(filePath)
                .toAbsolutePath()
                .normalize()
                .toString();

        try {

            String baselineHash =
                    baselineService.getBaseline(filePath);

            if (baselineHash == null) {

                return new IntegrityResult(
                        resourceId,
                        IntegrityStatus.ERROR,
                        null,
                        null,
                        Instant.now()
                );
            }

            IntegrityBaseline baseline =
                    new IntegrityBaseline(
                            resourceId,
                            "filesystem-local",
                            "SHA-256",
                            baselineHash,
                            Instant.now()
                    );

            return integrityEvaluator.evaluate(
                    Path.of(filePath),
                    baseline
            );

        } catch (IOException exception) {

            return new IntegrityResult(
                    resourceId,
                    IntegrityStatus.ERROR,
                    null,
                    null,
                    Instant.now()
            );
        }
    }
}