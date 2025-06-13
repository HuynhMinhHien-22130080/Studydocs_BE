package com.mobile.studydocs.service;

import com.google.firebase.messaging.*;
import com.mobile.studydocs.model.message.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseNotificationService {
    private final FirebaseMessaging firebaseMessaging;


    public void sendNotification(List<String> fcmTokens, NotificationMessage notificationMessage) {
        Notification notification = Notification.builder()
                .setTitle(notificationMessage.title())
                .setBody(notificationMessage.message())
                .build();

        if (fcmTokens.isEmpty()) {
            log.warn("No valid FCM tokens found");
            return;
        }

        MulticastMessage multicastMessage = MulticastMessage.builder()
                .setNotification(notification)
                .addAllTokens(fcmTokens)
                .build();


        BatchResponse response = null;
        try {
            response = firebaseMessaging.sendEachForMulticast
                    (multicastMessage);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
        if (response.getFailureCount() > 0) {
            for (int i = 0; i < response.getResponses().size(); i++) {
                SendResponse sendResponse = response.getResponses().get(i);
                if (!sendResponse.isSuccessful()) {
                    log.warn("Failed to send to token {}...: {}",
                            fcmTokens.get(i).substring(0, Math.min(10, fcmTokens.get(i).length())),
                            sendResponse.getException().getMessage());
                }
            }
        }
    }
}

