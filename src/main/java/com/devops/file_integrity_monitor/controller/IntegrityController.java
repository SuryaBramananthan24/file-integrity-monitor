package com.devops.file_integrity_monitor.controller;

import com.devops.file_integrity_monitor.service.FileHashService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IntegrityController {

    private final FileHashService fileHashService;
    public IntegrityController(FileHashService fileHashService) {
        this.fileHashService = fileHashService;
    }

    @GetMapping("/api/hash")
    public String generateHash(@RequestParam String path) throws Exception {
        return fileHashService.generateHash(path);
    }
}