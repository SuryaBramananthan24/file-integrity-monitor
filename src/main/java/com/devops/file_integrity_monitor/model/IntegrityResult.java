package com.devops.file_integrity_monitor.model;

public class IntegrityResult {

    private String filePath;
    private String baselineHash;
    private String currentHash;
    private boolean integrityValid;
    private String message;

    public IntegrityResult(
            String filePath,
            String baselineHash,
            String currentHash,
            boolean integrityValid,
            String message) {

        this.filePath = filePath;
        this.baselineHash = baselineHash;
        this.currentHash = currentHash;
        this.integrityValid = integrityValid;
        this.message = message;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getBaselineHash() {
        return baselineHash;
    }

    public String getCurrentHash() {
        return currentHash;
    }

    public boolean isIntegrityValid() {
        return integrityValid;
    }

    public String getMessage() {
        return message;
    }
}