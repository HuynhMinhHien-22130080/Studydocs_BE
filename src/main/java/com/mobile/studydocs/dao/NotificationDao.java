package com.mobile.studydocs.dao;

import com.google.cloud.firestore.Firestore;
import com.mobile.studydocs.model.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationDao {
    private static final String USER_COLLECTION = "users";
    private static final String NOTIFICATION_COLLECTION = "notifications";
    private final Firestore firestore;

    public List<Notification> getNotifications(String userId) {
        try {
            return firestore.collection(USER_COLLECTION)
                    .document(userId)
                    .collection(NOTIFICATION_COLLECTION)
                    .get()
                    .get()
                    .toObjects(Notification.class);
        } catch (Exception e) {
            log.error("Error getting notifications for userId={}: {}", userId, e.getMessage(), e);
            return null;
        }
    }

    public boolean addNotification(String userId, Notification notification) {
        try {
            firestore.collection(USER_COLLECTION)
                    .document(userId)
                    .collection(NOTIFICATION_COLLECTION)
                    .add(notification)
                    .get();
            return true;
        } catch (Exception e) {
            log.error("Error adding notification for userId={}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    public boolean deleteNotification(String userId, String notificationId) {
        try {
            firestore.collection(USER_COLLECTION)
                    .document(userId)
                    .collection(NOTIFICATION_COLLECTION)
                    .document(notificationId)
                    .delete()
                    .get();
            log.info("Removed notification {} for userId={}", notificationId, userId);
            return true;
        } catch (Exception e) {
            log.error("Error removing notification {} for userId={}: {}", notificationId, userId, e.getMessage(), e);
            return false;
        }
    }

    public boolean deleteAllNotification(String userId) {
        try {
            var collection = firestore.collection(USER_COLLECTION)
                    .document(userId)
                    .collection(NOTIFICATION_COLLECTION)
                    .get()
                    .get();
            for (var doc : collection.getDocuments()) {
                doc.getReference().delete();
            }
            log.info("Removed all notifications for userId={}", userId);
            return true;
        } catch (Exception e) {
            log.error("Error removing all notifications for userId={}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    public boolean readNotification(String userId, String notificationId) {
        try {
            firestore.collection(USER_COLLECTION)
                    .document(userId)
                    .collection(NOTIFICATION_COLLECTION)
                    .document(notificationId)
                    .update("isRead", true)
                    .get();
            log.info("Marked notification {} as read for userId={}", notificationId, userId);
            return true;
        } catch (Exception e) {
            log.error("Error marking notification {} as read for userId={}: {}", notificationId, userId, e.getMessage(), e);
            return false;
        }
    }

    public boolean readAllNotification(String userId) {
        try {
            var collection = firestore.collection(USER_COLLECTION)
                    .document(userId)
                    .collection(NOTIFICATION_COLLECTION)
                    .get()
                    .get();

            for (var doc : collection.getDocuments()) {
                doc.getReference().update("isRead", true);
            }
            log.info("Marked all notifications as read for userId={}", userId);
            return true;
        } catch (Exception e) {
            log.error("Error marking all notifications as read for userId={}: {}", userId, e.getMessage(), e);
            return false;
        }
    }
}
