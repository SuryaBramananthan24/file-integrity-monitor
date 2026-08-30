package com.devops.file_integrity_monitor.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final Counter integrityChecks;
    private final Counter successfulChecks;
    private final Counter integrityViolations;
    private final Counter checkFailures;

    public MetricsService(MeterRegistry meterRegistry) {
        integrityChecks = Counter.builder("integrity.checks.total").description("Total number of file integrity checks").register(meterRegistry);
        successfulChecks = Counter.builder("integrity.checks.success.total").description("Total successful file integrity checks").register(meterRegistry);
        integrityViolations = Counter.builder("integrity.violations.total").description("Total number of file integrity violations").register(meterRegistry);
        checkFailures = Counter.builder("integrity.check.failures.total").description("Total number of integrity check failures").register(meterRegistry);
    }

    public void incrementIntegrityChecks() {
        integrityChecks.increment();
    }

    public void incrementSuccessfulChecks() {
        successfulChecks.increment();
    }

    public void incrementViolations() {
        integrityViolations.increment();
    }

    public void incrementFailures() {
        checkFailures.increment();
    }
}