package com.devops.file_integrity_monitor.scheduler;

import com.devops.file_integrity_monitor.model.IntegrityResult;
import com.devops.file_integrity_monitor.service.IntegrityMonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.devops.file_integrity_monitor.service.MetricsService;

@Component
public class IntegrityCheckScheduler {

    private static final Logger logger = LoggerFactory.getLogger(IntegrityCheckScheduler.class);
    private final IntegrityMonitoringService integrityMonitoringService;
    @Value("${monitoring.file-path}")
    private String monitoredFilePath;
    private final MetricsService metricsService;

    public IntegrityCheckScheduler(IntegrityMonitoringService integrityMonitoringService,MetricsService metricsService) {
        this.integrityMonitoringService = integrityMonitoringService;
        this.metricsService = metricsService;
    }
    @Scheduled(fixedRateString = "${monitoring.interval}")
    public void monitorFileIntegrity() {
        try {
            IntegrityResult result = integrityMonitoringService.checkIntegrity(monitoredFilePath);
            metricsService.incrementIntegrityChecks();
            if (result.isIntegrityValid()) {
                metricsService.incrementSuccessfulChecks();
                logger.info("File integrity check PASSED for: {}",monitoredFilePath);
            }
            else {
                metricsService.incrementViolations();
                logger.warn("FILE INTEGRITY VIOLATION detected for: {}. Reason: {}", monitoredFilePath, result.getMessage());
            }
        }
        catch (Exception exception) {
            metricsService.incrementFailures();
            logger.error("Error while monitoring file: {}", monitoredFilePath, exception);
        }
    }
}