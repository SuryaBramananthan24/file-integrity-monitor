package com.devops.file_integrity_monitor.event;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class KafkaIntegrityEventPublisherTest {

    @Test
    public void shouldPublishIntegrityEvent() {

        SoftAssert softAssert = new SoftAssert();

        KafkaTemplate<String, IntegrityEvent> kafkaTemplate =
                mock(KafkaTemplate.class);

        KafkaIntegrityEventPublisher publisher =
                new KafkaIntegrityEventPublisher(
                        kafkaTemplate,
                        "integrity-events"
                );

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

        publisher.publish(event);

        verify(kafkaTemplate).send(
                eq("integrity-events"),
                eq("filesystem-local"),
                eq(event)
        );

        softAssert.assertNotNull(event.eventId(), "Event ID should exist");
        softAssert.assertEquals(event.schemaVersion(), 1);
        softAssert.assertEquals(event.sourceId(), "filesystem-local");
        softAssert.assertEquals(event.resourceId(), "/tmp/test.txt");
        softAssert.assertEquals(event.eventType(), "MODIFIED");
        softAssert.assertEquals(event.oldDigest(), "old-digest");
        softAssert.assertEquals(event.newDigest(), "new-digest");

        softAssert.assertAll();
    }
}