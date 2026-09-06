package com.devops.file_integrity_monitor.event;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IntegrityEventIdempotencyService {

    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public boolean shouldProcess(String eventId) {
        return !processedEventIds.contains(eventId);
    }

    public void markProcessed(String eventId) {
        processedEventIds.add(eventId);
    }
}