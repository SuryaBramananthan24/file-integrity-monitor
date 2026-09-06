package com.devops.file_integrity_monitor.audit;

import java.time.Instant;

public record IntegrityAuditEvent(String eventId,String sourceId,String resourceId,String eventType,String oldDigest,String newDigest,Instant occurredAt) {}