package com.mobile.studydocs.dao;

import com.google.cloud.firestore.Firestore;
import com.mobile.studydocs.exception.NotificationNotFound;
import com.mobile.studydocs.model.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationDao {
    private static final String USER_COLLECTION = "users";
    private static final String NOTIFICATION_COLLECTION = "notifications";
    private final Firestore firestore;

    private boolean userNotExists(String userId) throws ExecutionException, InterruptedException {
        return !firestore.collection(USER_COLLECTION).document(userId).get().get().exists();
    }

    private boolean notificationNotExists(String userId, String notificationId) throws ExecutionException, InterruptedException {
        return !firestore.collection(USER_COLLECTION)
                .document(userId)
                .collection(NOTIFICATION_COLLECTION)
                .document(notificationId)
                .get()
                .get()
                .exists();
    }

    public List<Notification> getNotifications(String userId) {
        try {
            if (userNotExists(userId)) {
                throw new RuntimeException("Người dùng không tồn tại");
            }
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

    public void addNotification(String userId, Notification notification) throws ExecutionException, InterruptedException {
            if (userNotExists(userId)) {
                throw new RuntimeException("Người dùng không tồn tại");
            }
            firestore.collection(USER_COLLECTION)
                    .document(userId)
                    .collection(NOTIFICATION_COLLECTION)
                    .add(notification)
                    .get();
    }

    public void deleteNotification(String userId, String notificationId) throws ExecutionException, InterruptedException {
        if (userNotExists(userId)) {
            throw new RuntimeException("Người dùng không tồn tại");
        }
        if (notificationNotExists(userId, notificationId)) {
            throw new NotificationNotFound("Thông báo không tồn tại", HttpStatus.NOT_FOUND, "NOTFOUND");
        }
        firestore.collection(USER_COLLECTION)
                .document(userId)
                .collection(NOTIFICATION_COLLECTION)
                .document(notificationId)
                .delete()
                .get();

    }

    public void deleteAllNotification(String userId) throws ExecutionException, InterruptedException {
        if (userNotExists(userId)) {
            throw new RuntimeException("Người dùng không tồn tại");
        }
        var collection = firestore.collection(USER_COLLECTION)
                .document(userId)
                .collection(NOTIFICATION_COLLECTION)
                .get()
                .get();
        if (collection.isEmpty()) {
            throw new NotificationNotFound("Thông báo không tồn tại", HttpStatus.NOT_FOUND, "NOTFOUND");
        }
        for (var doc : collection.getDocuments()) {
            doc.getReference().delete();
        }
    }

    public void readNotification(String userId, String notificationId) throws ExecutionException, InterruptedException {
        if (userNotExists(userId)) {
            throw new RuntimeException("Người dùng không tồn tại");
        }
        if (notificationNotExists(userId, notificationId)) {
            throw new NotificationNotFound("Thông báo không tồn tại", HttpStatus.NOT_FOUND, "NOTFOUND");
        }
        firestore.collection(USER_COLLECTION)
                .document(userId)
                .collection(NOTIFICATION_COLLECTION)
                .document(notificationId)
                .update("isRead", true)
                .get();
    }

    public void readAllNotification(String userId) throws ExecutionException, InterruptedException {
        if (userNotExists(userId)) {
            throw new RuntimeException("Người dùng không tồn tại");
        }
        var collection = firestore.collection(USER_COLLECTION)
                .document(userId)
                .collection(NOTIFICATION_COLLECTION)
                .get()
                .get();
        if (collection.isEmpty()) {
            throw new NotificationNotFound("Thông báo không tồn tại", HttpStatus.NOT_FOUND, "NOTFOUND");
        }
        for (var doc : collection.getDocuments()) {
            doc.getReference().update("isRead", true);
        }
    }
}
