package com.devops.file_integrity_monitor.integrity;

import java.time.Instant;

public record IntegrityBaseline(String resourceId,String sourceId,String algorithm,String digest,Instant createdAt) {}