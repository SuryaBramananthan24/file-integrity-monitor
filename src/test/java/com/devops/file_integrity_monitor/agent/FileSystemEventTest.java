package com.devops.file_integrity_monitor.agent;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Instant;


public class FileSystemEventTest {
    private SoftAssert softAssert = new SoftAssert();
    @Test
    public void shouldCreateFilesystemEvent() {
        Instant timestamp = Instant.now();
        FileSystemEvent event = new FileSystemEvent("filesystem-agent-01","/test/application.log",FileSystemEventType.MODIFIED,timestamp);
        softAssert.assertEquals(event.sourceId(),"filesystem-agent-01");
        softAssert.assertEquals(event.resourceId(), "/test/application.log");
        softAssert.assertEquals(event.eventType(),FileSystemEventType.MODIFIED);
        softAssert.assertNotNull(event.occurredAt());
        softAssert.assertEquals(event.occurredAt(), timestamp);
    }

    @Test
    public void shouldSupportAllFilesystemEventTypes() {
        softAssert.assertEquals(FileSystemEventType.values().length,3);
    }
}