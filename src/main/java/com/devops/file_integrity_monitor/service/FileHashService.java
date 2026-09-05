package com.devops.file_integrity_monitor.service;

import com.devops.file_integrity_monitor.integrity.DigestService;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Path;

@Service
public class FileHashService {
    private final DigestService digestService;
    public FileHashService(DigestService digestService) {
        this.digestService = digestService;
    }

    public String calculateHash(String filePath) throws IOException {
        return digestService.calculate(Path.of(filePath));
    }
}