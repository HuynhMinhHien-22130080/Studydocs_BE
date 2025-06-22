package com.mobile.studydocs.service;

import com.google.firebase.messaging.*;
import com.mobile.studydocs.model.message.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseNotificationService {

    private final FirebaseMessaging firebaseMessaging;

    public Map<String, List<String>> sendNotification(Map<String, List<String>> userTokensMap, NotificationMessage notificationMessage) {
        Notification notification = Notification.builder()
                .setTitle(notificationMessage.title())
                .setBody(notificationMessage.message())
                .build();

        List<String> allTokens = new ArrayList<>();
        Map<String, String> tokenToUserId = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : userTokensMap.entrySet()) {
            String userId = entry.getKey();
            for (String token : entry.getValue()) {
                allTokens.add(token);
                tokenToUserId.put(token, userId);
            }
        }

        if (allTokens.isEmpty()) {
            log.warn("No FCM tokens provided.");
            return Collections.emptyMap();
        }

        MulticastMessage multicastMessage = MulticastMessage.builder()
                .setNotification(notification)
                .addAllTokens(allTokens)
                .build();

        try {
            BatchResponse response = firebaseMessaging.sendEachForMulticast(multicastMessage);
            log.error("Successfully sent messages: {}", response.getSuccessCount());
            return extractInvalidTokens(response, allTokens, tokenToUserId);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send notification", e);
            throw new RuntimeException(e);
        }
    }

    private Map<String, List<String>> extractInvalidTokens(BatchResponse response, List<String> allTokens, Map<String, String> tokenToUserId) {
        Map<String, List<String>> invalidTokensByUser = new HashMap<>();

        for (int i = 0; i < response.getResponses().size(); i++) {
            SendResponse sendResponse = response.getResponses().get(i);
            if (!sendResponse.isSuccessful()) {
                String token = allTokens.get(i);
                FirebaseMessagingException exception = sendResponse.getException();
                MessagingErrorCode errorCode = exception.getMessagingErrorCode();

                if (errorCode == MessagingErrorCode.UNREGISTERED || errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
                    String userId = tokenToUserId.get(token);
                    invalidTokensByUser
                            .computeIfAbsent(userId, k -> new ArrayList<>())
                            .add(token);
                    log.warn("Invalid token [{}] for user [{}]", token, userId);
                }
            }
        }

        return invalidTokensByUser;
    }
}
