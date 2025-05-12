package com.mobile.studydocs.kafka.producer;

import com.mobile.studydocs.event.EventPublisher;
import com.mobile.studydocs.model.event.DocumentViewedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DocViewedEventPublisher implements EventPublisher<DocumentViewedEvent> {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "document-viewed";

    public DocViewedEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(DocumentViewedEvent event) {
        kafkaTemplate.send(TOPIC, event.getUserId(), event);
    }
}
