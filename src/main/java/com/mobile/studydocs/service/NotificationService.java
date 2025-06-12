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
        return notificationDao.getNotifications(userId);
    }

    public void addNotification(String userId, Notification notification) {
        if (notificationDao.addNotification(userId, notification)) {
            String followType = NotificationType.getFollowType(notification.getType()).toString();
            List<String> tokens = followService.getFCMTokensNeedNotify(userId, notification.getTargetId(), followType);
            String senderName = userService.findUserById(notification.getSenderId()).getFullName();
            notificationEventProducer.publish(new NotificationCreateEvent(tokens, notification.getType(), senderName));
        }
    }

    public boolean deleteNotification(String userId, String notificationId) {
        return notificationDao.deleteNotification(userId, notificationId);
    }

    public boolean deleteAllNotifications(String userId) {
        return notificationDao.deleteAllNotification(userId);
    }

    public boolean markAsRead(String userId, String notificationId) {
        return notificationDao.readNotification(userId, notificationId);
    }

    public boolean markAllAsRead(String userId) {
        return notificationDao.readAllNotification(userId);
    }
}
