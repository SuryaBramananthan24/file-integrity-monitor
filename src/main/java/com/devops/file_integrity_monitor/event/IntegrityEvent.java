package com.devops.file_integrity_monitor.event;

import java.time.Instant;

public record IntegrityEvent(String eventId,int schemaVersion,String sourceId,String resourceId,String eventType,Instant occurredAt,String oldDigest,String newDigest) {}