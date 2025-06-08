package com.mobile.studydocs.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.mobile.studydocs.dao.DocumentDao;
import com.mobile.studydocs.dao.FollowerDao;
import com.mobile.studydocs.model.entity.Notifications;
import com.mobile.studydocs.model.event.DocumentUploadedEvent;
import com.mobile.studydocs.model.messaging.DocumentsMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private static final String NEW_DOCUMENT_NOTIFICATION_TITLE = "Bài viết mới";

    private final FirebaseNotificationService firebaseNotificationService;
    private final FollowerDao userDao;
    private final DocumentDao documentDao;


    @Async
    public void notify(DocumentUploadedEvent event) {
        logger.info("Xử lý thông báo: docId={}, userId={}", event.getDocumentId(), event.getUserId());
        try {
            DocumentsMessage document = new DocumentsMessage(documentDao.findById(event.getDocumentId()));
            Map<String, String[]> fcmTokens = userDao.getFCMTokensForNotifiableFollowers(event.getUserId());

            if (fcmTokens.isEmpty()) {
                logger.info("Không có follower nào cần thông báo cho docId={}", event.getDocumentId());
                return;
            }

            logger.info("Gửi thông báo cho {} follower của docId={}", fcmTokens.size(), event.getDocumentId());

            for (Map.Entry<String, String[]> entry : fcmTokens.entrySet()) {
                String followerId = entry.getKey();
                String[] tokens = entry.getValue();

                //Tạo notification
                Notifications notifications = Notifications.builder()
                        .senderId(event.getUserId())
                        .documentId(event.getDocumentId())
                        .type("new_document")
                        .title(NEW_DOCUMENT_NOTIFICATION_TITLE)
                        .message(document.getDescription())
                        .build();
                userDao.addNotification(followerId, notifications);

                //Gửi FCM Token
                int successCount = 0;
                int invalidTokenCount = 0;

                for (String token : tokens) {
                    try {
                        firebaseNotificationService.sendNotification(token, NEW_DOCUMENT_NOTIFICATION_TITLE, notifications.getMessage());
                        successCount++;
                    } catch (FirebaseMessagingException e) {
                        MessagingErrorCode errorCode = e.getMessagingErrorCode();
                        if (errorCode == MessagingErrorCode.UNREGISTERED) {
                            userDao.removeFCMToken(followerId, token);
                            invalidTokenCount++;
                        }
                    }
                }

                if (invalidTokenCount > 0) {
                    logger.warn("Follower {} có {} token không hợp lệ đã bị xóa", followerId, invalidTokenCount);
                }
            }

            logger.info("Hoàn thành gửi thông báo cho docId={}", event.getDocumentId());
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Lỗi xử lý thông báo docId={}: {}", event.getDocumentId(), e.getMessage());
        }
    }


}

