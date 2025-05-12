package com.mobile.studydocs.service;

import com.mobile.studydocs.kafka.producer.DocViewedEventPublisher;
import com.mobile.studydocs.model.event.DocumentViewedEvent;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    private final DocViewedEventPublisher eventPublisher;

    public DocumentService(DocViewedEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void viewDocument(String userId, String documentId) {
        // Gọi khi người dùng xem chi tiết tài liệu
        DocumentViewedEvent event = new DocumentViewedEvent(userId, documentId, System.currentTimeMillis());
        eventPublisher.publish(event);

        // Các xử lý khác nếu cần
    }
}

