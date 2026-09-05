package com.devops.file_integrity_monitor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.devops.file_integrity_monitor.integrity.IntegrityResult;
import com.devops.file_integrity_monitor.service.IntegrityMonitoringService;
import com.devops.file_integrity_monitor.service.FileHashService;
import com.devops.file_integrity_monitor.service.BaselineService;


@RestController
public class IntegrityController {

    private final FileHashService fileHashService;
    private final BaselineService baselineService;
    private final IntegrityMonitoringService integrityMonitoringService;

    public IntegrityController(FileHashService fileHashService,BaselineService baselineService,IntegrityMonitoringService integrityMonitoringService) {
        this.fileHashService = fileHashService;
        this.baselineService = baselineService;
        this.integrityMonitoringService = integrityMonitoringService;
    }


    @GetMapping("/api/hash")
    public String generateHash(@RequestParam String path)throws Exception {
        return fileHashService.calculateHash(path);
    }

    @PostMapping("/api/baseline")
    public String initializeBaseline(@RequestParam String path)throws Exception {
        String hash = fileHashService.calculateHash(path);
        baselineService.saveBaseline(path,hash);
        return "Baseline initialized successfully for: "+ path;
    }

    @GetMapping("/api/integrity/check")
    public IntegrityResult checkIntegrity(@RequestParam String path) throws Exception{
        return integrityMonitoringService.checkIntegrity(path);
    }
}