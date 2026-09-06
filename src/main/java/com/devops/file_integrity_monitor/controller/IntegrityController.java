package com.devops.file_integrity_monitor.controller;

import com.devops.file_integrity_monitor.integrity.IntegrityBaseline;
import com.devops.file_integrity_monitor.integrity.IntegrityResult;
import com.devops.file_integrity_monitor.service.IntegrityMonitoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class IntegrityController {

    private final IntegrityMonitoringService integrityMonitoringService;

    public IntegrityController(
            IntegrityMonitoringService integrityMonitoringService) {

        this.integrityMonitoringService =
                integrityMonitoringService;
    }

    /**
     * Create or update the PostgreSQL integrity baseline
     * for a file.
     */
    @PostMapping("/baseline")
    public ResponseEntity<?> createBaseline(
            @RequestParam String path) {

        try {

            IntegrityBaseline baseline =
                    integrityMonitoringService.createBaseline(path);

            return ResponseEntity.ok(baseline);

        } catch (Exception exception) {

            return ResponseEntity.badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    "Unable to create integrity baseline",
                                    "message",
                                    exception.getMessage()
                            )
                    );
        }
    }

    /**
     * Retrieve the PostgreSQL baseline for a file.
     */
    @GetMapping("/baseline")
    public ResponseEntity<?> getBaseline(
            @RequestParam String path) {

        Optional<IntegrityBaseline> baseline =
                integrityMonitoringService.getBaseline(path);

        if (baseline.isEmpty()) {

            return ResponseEntity.notFound()
                    .build();
        }

        return ResponseEntity.ok(
                baseline.get()
        );
    }

    /**
     * Check the current file against
     * its PostgreSQL baseline.
     */
    @GetMapping("/integrity/check")
    public ResponseEntity<IntegrityResult> checkIntegrity(
            @RequestParam String path) {

        IntegrityResult result =
                integrityMonitoringService.checkIntegrity(path);

        return ResponseEntity.ok(result);
    }
}