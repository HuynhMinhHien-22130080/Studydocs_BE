package com.mobile.studydocs.kafka.producer;

import com.mobile.studydocs.event.EventPublisher;
import com.mobile.studydocs.model.event.DocSearchEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
@Component
public class DocSearchEventPublisher implements EventPublisher<DocSearchEvent> {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "document-search";
    public DocSearchEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    @Override
    public void publish(DocSearchEvent event) {
        kafkaTemplate.send(TOPIC, event.getUserId(), event);

    }
}
