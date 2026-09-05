package com.devops.file_integrity_monitor.scheduler;

import com.devops.file_integrity_monitor.integrity.IntegrityResult;
import com.devops.file_integrity_monitor.integrity.IntegrityStatus;
import com.devops.file_integrity_monitor.service.IntegrityMonitoringService;
import com.devops.file_integrity_monitor.service.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IntegrityCheckScheduler {

    private static final Logger logger = LoggerFactory.getLogger(IntegrityCheckScheduler.class);
    private final IntegrityMonitoringService integrityMonitoringService;
    private final MetricsService metricsService;

    @Value("${monitoring.file-path}")
    private String monitoredFilePath;

    public IntegrityCheckScheduler(IntegrityMonitoringService integrityMonitoringService,MetricsService metricsService) {
        this.integrityMonitoringService = integrityMonitoringService;
        this.metricsService = metricsService;
    }

    @Scheduled(fixedRateString = "${monitoring.interval}")
    public void monitorFileIntegrity() {
        try {
            IntegrityResult result = integrityMonitoringService.checkIntegrity(monitoredFilePath);
            metricsService.incrementIntegrityChecks();

            if (result.status() == IntegrityStatus.UNCHANGED) {
                metricsService.incrementSuccessfulChecks();
                logger.info("File integrity check PASSED for: {}",monitoredFilePath);
            }
            else if (result.status() == IntegrityStatus.CHANGED) {
                metricsService.incrementViolations();
                logger.warn("FILE INTEGRITY VIOLATION detected for: {}. " +"Previous digest: {}, Current digest: {}",monitoredFilePath,result.previousDigest(),result.currentDigest());
            }
            else if (result.status() == IntegrityStatus.UNAVAILABLE) {
                metricsService.incrementFailures();
                logger.warn("FILE INTEGRITY CHECK UNAVAILABLE for: {}",monitoredFilePath);
            }
            else {
                metricsService.incrementFailures();
                logger.error("FILE INTEGRITY CHECK FAILED for: {}",monitoredFilePath);
            }
        }
        catch (Exception exception) {
            metricsService.incrementFailures();
            logger.error("Error while monitoring file: {}",monitoredFilePath,exception);
        }
    }
}