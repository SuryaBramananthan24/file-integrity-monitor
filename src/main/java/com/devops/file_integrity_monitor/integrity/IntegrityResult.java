package com.devops.file_integrity_monitor.integrity;

import java.time.Instant;

public record IntegrityResult(String resourceId,IntegrityStatus status,String previousDigest,String currentDigest,Instant checkedAt) {}