package com.devops.file_integrity_monitor.controller;

import com.devops.file_integrity_monitor.service.BaselineService;
import com.devops.file_integrity_monitor.service.FileHashService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IntegrityController {

    private final FileHashService fileHashService;
    private final BaselineService baselineService;

    public IntegrityController(FileHashService fileHashService,BaselineService baselineService) {
        this.fileHashService = fileHashService;
        this.baselineService = baselineService;
    }


    @GetMapping("/api/hash")
    public String generateHash(@RequestParam String path)throws Exception {
        return fileHashService.generateHash(path);
    }

    @PostMapping("/api/baseline")
    public String initializeBaseline(@RequestParam String path)throws Exception {
        String hash =fileHashService.generateHash(path);
        baselineService.saveBaseline(path,hash);
        return "Baseline initialized successfully for: "+ path;
    }
}