package com.devops.file_integrity_monitor.service;


import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class BaselineService {

    private static final String BASELINE_FILE =
            "data/baseline.json";

    private final ObjectMapper objectMapper;


    public BaselineService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Map<String, String> loadBaselines() throws IOException {

        File file = new File(BASELINE_FILE);
        if (!file.exists() || file.length() == 0) {
            return new HashMap<>();
        }
        return objectMapper.readValue(file,new TypeReference<Map<String, String>>() {});
    }

    public void saveBaselines(
            Map<String, String> baselines)
            throws IOException {

        File file = new File(BASELINE_FILE);

        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(file, baselines);
    }

    public void saveBaseline(
            String filePath,
            String hash)
            throws IOException {

        Map<String, String> baselines =
                loadBaselines();

        baselines.put(filePath, hash);

        saveBaselines(baselines);
    }

    public String getBaseline(
            String filePath)
            throws IOException {

        Map<String, String> baselines =
                loadBaselines();

        return baselines.get(filePath);
    }
}