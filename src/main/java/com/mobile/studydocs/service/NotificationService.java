
package com.mobile.studydocs.service;

import com.mobile.studydocs.dao.NotificationDao;
import com.mobile.studydocs.event.model.NotificationCreateEvent;
import com.mobile.studydocs.event.rabbitmq.producer.NotificationEventProducer;
import com.mobile.studydocs.exception.NotificationNotFound;
import com.mobile.studydocs.model.entity.Notification;
import com.mobile.studydocs.model.enums.FollowType;
import com.mobile.studydocs.model.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
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

    public void addNotification(List<String> userIdList, Notification notification) {
        List<String> successfulUserIds = new ArrayList<>();
        try {
            userIdList.forEach(
                    (userId) -> {
                        try {
                            notificationDao.addNotification(userId, notification);
                            successfulUserIds.add(userId);
                        } catch (ExecutionException | InterruptedException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
            );
        } catch (NotificationNotFound e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Không thể thêm thông báo");
        }
        String followType = NotificationType.getFollowType(notification.getType()).toString();
        Map<String, List<String>> tokensByUser = _getMapFcmToken(successfulUserIds, notification.getTargetId(), FollowType.valueOf(followType));
        String senderName = userService.findUserById(notification.getSenderId()).getFullName();
        System.out.println(tokensByUser);
        notificationEventProducer.publish(new NotificationCreateEvent(tokensByUser, notification.getType(), senderName));
    }

    public void deleteNotification(String userId, String notificationId) {
        try {
            notificationDao.deleteNotification(userId, notificationId);
        } catch (NotificationNotFound e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Không thể xóa thông báo");
        }
    }

    public void deleteAllNotifications(String userId) {
        try {
            notificationDao.deleteAllNotification(userId);
        } catch (
                NotificationNotFound e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Không thể xóa tất cả thông báo");
        }

    }

    public void markAsRead(String userId, String notificationId) {
        try {
            notificationDao.readNotification(userId, notificationId);
        } catch (
                NotificationNotFound e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Không thể đánh dấu tất cả đã đọc");
        }
    }

    public void markAllAsRead(String userId) {
        try {
            notificationDao.readAllNotification(userId);
        } catch (NotificationNotFound e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Không thể đánh dấu đã đọc");
        }
    }

    /**
     * Lấy mapping theo user và token
     */
    private Map<String, List<String>> _getMapFcmToken(List<String> userIdList, String targetId, FollowType followType) {
        Map<String, List<String>> tokensByUser = new HashMap<>();
        userIdList.forEach(
                (userId) -> {
                    List<String> tokens = followService.getFCMTokensNeedNotify(userId, targetId, followType.toString());
                    tokensByUser.put(userId, tokens);
                }
        );
        return tokensByUser;
    }
}
