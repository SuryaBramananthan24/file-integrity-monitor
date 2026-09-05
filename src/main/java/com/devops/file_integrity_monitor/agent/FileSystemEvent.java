package com.devops.file_integrity_monitor.agent;
import java.time.Instant;

public record FileSystemEvent(String sourceId,String resourceId,FileSystemEventType eventType,Instant occurredAt) {}