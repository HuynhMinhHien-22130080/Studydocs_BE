package com.mobile.studydocs.service;

import com.mobile.studydocs.dao.NotificationDao;
import com.mobile.studydocs.event.model.NotificationCreateEvent;
import com.mobile.studydocs.event.rabbitmq.producer.NotificationEventProducer;
import com.mobile.studydocs.model.entity.Notification;
import com.mobile.studydocs.model.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationDao notificationDao;
    private final NotificationEventProducer notificationEventProducer;
    private final FollowService followService;
    private final UserService userService;

    public List<Notification> getNotifications(String userId) {
        List<Notification> notifications = notificationDao.getNotifications(userId);
        if (notifications != null) {
            return notifications;
        } else {
            throw new RuntimeException("Lỗi không thể lấy thông báo" + userId);
        }
    }

    public void addNotification(String userId, Notification notification) {
        if (notificationDao.addNotification(userId, notification)) {
            String followType = NotificationType.getFollowType(notification.getType()).toString();
            List<String> tokens = followService.getFCMTokensNeedNotify(userId, notification.getTargetId(), followType);
            String senderName = userService.findUserById(notification.getSenderId()).getFullName();
            notificationEventProducer.publish(new NotificationCreateEvent(tokens, notification.getType(), senderName));
        }
    }

    public void deleteNotification(String userId, String notificationId) {
        if (!notificationDao.deleteNotification(userId, notificationId))
            throw new RuntimeException("Không thể xóa thông báo");

    }

    public void deleteAllNotifications(String userId) {
        if (!notificationDao.deleteAllNotification(userId))
            throw new RuntimeException("Không thể xóa tất cả thông báo");

    }

    public boolean markAsRead(String userId, String notificationId) {
        return notificationDao.readNotification(userId, notificationId);
    }

    public boolean markAllAsRead(String userId) {
        return notificationDao.readAllNotification(userId);
    }
}
