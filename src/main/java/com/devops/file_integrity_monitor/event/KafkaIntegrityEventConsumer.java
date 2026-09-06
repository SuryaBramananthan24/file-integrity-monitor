package com.devops.file_integrity_monitor.event;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "integrity.kafka.enabled",havingValue = "true")
public class KafkaIntegrityEventConsumer {

    private final IntegrityEventProcessor processor;
    public KafkaIntegrityEventConsumer(IntegrityEventProcessor processor) {
        this.processor = processor;
    }

    @KafkaListener(topics = "${integrity.kafka.topic}",groupId = "${integrity.kafka.consumer.group-id}")
    public void consume(IntegrityEvent event) {
        processor.process(event);
    }
}