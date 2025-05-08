package com.mobile.studydocs.eventdriven.producer;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DocumentUploadProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "document-uploaded";

    public DocumentUploadProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendDocumentUploadedEvent(String documentDetailsJson) {
        kafkaTemplate.send(TOPIC, documentDetailsJson);
    }
}

