package com.devops.file_integrity_monitor.event;

import org.springframework.stereotype.Service;

@Service
public class IntegrityEventProcessor {

    private final IntegrityEventIdempotencyService idempotencyService;

    public IntegrityEventProcessor(IntegrityEventIdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }

    public void process(IntegrityEvent event) {

        if (!idempotencyService.shouldProcess(event.eventId())) {
            return;
        }
        idempotencyService.markProcessed(event.eventId());
    }
}