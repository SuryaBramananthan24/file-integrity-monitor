package com.devops.file_integrity_monitor.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        name = "integrity.kafka.enabled",
        havingValue = "true"
)
public class KafkaIntegrityEventPublisher {

    private final KafkaTemplate<String, IntegrityEvent> kafkaTemplate;
    private final String topic;

    public KafkaIntegrityEventPublisher(
            KafkaTemplate<String, IntegrityEvent> kafkaTemplate,
            @Value("${integrity.kafka.topic}") String topic) {

        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(IntegrityEvent event) {
        kafkaTemplate.send(
                topic,
                event.sourceId(),
                event
        );
    }
}