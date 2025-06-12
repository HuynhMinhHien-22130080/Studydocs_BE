package com.mobile.studydocs.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.mobile.studydocs.model.message.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseNotificationService {
    private final FirebaseMessaging firebaseMessaging;


    public void sendNotification(String fcmToken, NotificationMessage notificationMessage) {
        try {
            Notification notification = Notification.builder()
                    .setTitle(notificationMessage.title())
                    .setBody(notificationMessage.message())
                    .build();

            Message fcmMessage = Message.builder()
                    .setNotification(notification)
                    .setToken(fcmToken)
                    .build();

            firebaseMessaging.send(fcmMessage);
        } catch (FirebaseMessagingException e) {
            log.error("Lỗi gửi FCM: token={}, error={}", fcmToken, e.getMessage());
        }
    }
}
