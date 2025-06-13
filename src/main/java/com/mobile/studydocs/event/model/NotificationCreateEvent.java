package com.mobile.studydocs.event.model;

import com.mobile.studydocs.model.enums.NotificationType;

import java.util.List;

public record NotificationCreateEvent(List<String> receiverTokens, NotificationType type, String senderName) {
}
