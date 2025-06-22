package com.mobile.studydocs.event.model;

import com.mobile.studydocs.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreateEvent {
    private Map<String, List<String>> receiverTokens;
    private NotificationType type;
    private String senderName;
}
