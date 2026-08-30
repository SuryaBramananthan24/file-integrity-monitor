package com.devops.file_integrity_monitor.service;

import com.devops.file_integrity_monitor.model.IntegrityResult;

import org.springframework.stereotype.Service;

@Service
public class IntegrityMonitoringService {

    private final FileHashService fileHashService;
    private final BaselineService baselineService;

    public IntegrityMonitoringService(FileHashService fileHashService,BaselineService baselineService) {
        this.fileHashService = fileHashService;
        this.baselineService = baselineService;
    }

    public IntegrityResult checkIntegrity(String filePath)throws Exception {
        String baselineHash = baselineService.getBaseline(filePath);
        if (baselineHash == null) {
            return new IntegrityResult(filePath,null,null,false,"No baseline found for this file");
        }
        String currentHash = fileHashService.generateHash(filePath);
        boolean integrityValid = baselineHash.equals(currentHash);
        String message;
        if (integrityValid) {
            message = "File integrity verified successfully";
        }
        else {
            message = "FILE INTEGRITY VIOLATION DETECTED";
        }
        return new IntegrityResult(filePath,baselineHash,currentHash,integrityValid,message);
    }
}