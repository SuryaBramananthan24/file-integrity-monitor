package com.devops.file_integrity_monitor.event;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Instant;

import static org.mockito.Mockito.*;

public class IntegrityEventProcessorTest {

    @Test
    public void shouldProcessNewEvent() {

        SoftAssert softAssert = new SoftAssert();

        IntegrityEventIdempotencyService idempotencyService =
                mock(IntegrityEventIdempotencyService.class);

        IntegrityEventProcessor processor =
                new IntegrityEventProcessor(idempotencyService);

        IntegrityEvent event = new IntegrityEvent(
                "event-123",
                1,
                "filesystem-local",
                "/tmp/test.txt",
                "MODIFIED",
                Instant.now(),
                "old-digest",
                "new-digest"
        );

        when(idempotencyService.shouldProcess("event-123"))
                .thenReturn(true);

        processor.process(event);

        verify(idempotencyService)
                .shouldProcess("event-123");

        verify(idempotencyService)
                .markProcessed("event-123");

        softAssert.assertTrue(true);
        softAssert.assertAll();
    }

    @Test
    public void shouldIgnoreDuplicateEvent() {

        IntegrityEventIdempotencyService idempotencyService =
                mock(IntegrityEventIdempotencyService.class);

        IntegrityEventProcessor processor =
                new IntegrityEventProcessor(idempotencyService);

        IntegrityEvent event = new IntegrityEvent(
                "event-123",
                1,
                "filesystem-local",
                "/tmp/test.txt",
                "MODIFIED",
                Instant.now(),
                "old-digest",
                "new-digest"
        );

        when(idempotencyService.shouldProcess("event-123"))
                .thenReturn(false);

        processor.process(event);

        verify(idempotencyService)
                .shouldProcess("event-123");

        verify(idempotencyService, never())
                .markProcessed("event-123");
    }
}