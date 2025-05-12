package com.mobile.studydocs.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentViewedEvent {
    private String userId;
    private String documentId;
    private long timestamp; // thời gian xem tài liệu (epoch millis)
}

