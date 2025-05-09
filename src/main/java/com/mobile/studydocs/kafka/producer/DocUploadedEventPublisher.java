package com.mobile.studydocs.kafka.producer;


import com.mobile.studydocs.event.EventPublisher;
import com.mobile.studydocs.model.event.DocumentUploadedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DocUploadedEventPublisher implements EventPublisher<DocumentUploadedEvent> {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "document-uploaded";

    public DocUploadedEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public void publish(DocumentUploadedEvent event) {
        kafkaTemplate.send(TOPIC, event.getUserId(), event);

    }
}

