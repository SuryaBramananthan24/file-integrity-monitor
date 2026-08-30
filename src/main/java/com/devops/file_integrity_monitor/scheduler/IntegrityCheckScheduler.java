package com.devops.file_integrity_monitor.scheduler;

import com.devops.file_integrity_monitor.model.IntegrityResult;
import com.devops.file_integrity_monitor.service.IntegrityMonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IntegrityCheckScheduler {

    private static final Logger logger = LoggerFactory.getLogger(IntegrityCheckScheduler.class);
    private final IntegrityMonitoringService integrityMonitoringService;
    @Value("${monitoring.file-path}")
    private String monitoredFilePath;

    public IntegrityCheckScheduler(IntegrityMonitoringService integrityMonitoringService) {
        this.integrityMonitoringService = integrityMonitoringService;
    }
    @Scheduled(fixedRateString = "${monitoring.interval}")
    public void monitorFileIntegrity() {
        try {
            IntegrityResult result = integrityMonitoringService.checkIntegrity(monitoredFilePath);
            if (result.isIntegrityValid()) {
                logger.info("File integrity check PASSED for: {}",monitoredFilePath);
            }
            else {
                logger.warn("FILE INTEGRITY VIOLATION detected for: {}. Reason: {}", monitoredFilePath, result.getMessage());
            }
        }
        catch (Exception exception) {
            logger.error("Error while monitoring file: {}", monitoredFilePath, exception);
        }
    }
}