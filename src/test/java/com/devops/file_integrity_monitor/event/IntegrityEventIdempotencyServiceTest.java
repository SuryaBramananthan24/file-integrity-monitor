package com.devops.file_integrity_monitor.event;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class IntegrityEventIdempotencyServiceTest {

    @Test
    public void shouldProcessNewEventOnlyOnce() {

        SoftAssert softAssert = new SoftAssert();

        IntegrityEventIdempotencyService service =
                new IntegrityEventIdempotencyService();

        String eventId = "event-123";

        softAssert.assertTrue(
                service.shouldProcess(eventId),
                "New event should be processed"
        );

        service.markProcessed(eventId);

        softAssert.assertFalse(
                service.shouldProcess(eventId),
                "Already processed event should be rejected"
        );

        softAssert.assertAll();
    }
}