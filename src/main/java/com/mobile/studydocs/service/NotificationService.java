
package com.mobile.studydocs.service;

import com.mobile.studydocs.dao.NotificationDao;
import com.mobile.studydocs.event.model.NotificationCreateEvent;
import com.mobile.studydocs.event.rabbitmq.producer.NotificationEventProducer;
import com.mobile.studydocs.exception.NotificationNotFound;
import com.mobile.studydocs.model.entity.Notification;
import com.mobile.studydocs.model.enums.FollowType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Iterator;
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

    public void addNotification(String targetId, Notification notification) {
        Map<String, List<String>> tokensByUser = followService.getFCMTokensNeedNotify(targetId, FollowType.USER);

        try {
            Iterator<Map.Entry<String, List<String>>> iterator = tokensByUser.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, List<String>> entry = iterator.next();
                String userId = entry.getKey();

                try {
                    notificationDao.addNotification(userId, notification);
                } catch (ExecutionException | InterruptedException e) {
                    log.error("Không thể lưu thông báo cho userId={}: {}", userId, e.getMessage(), e);
                    iterator.remove();
                }
            }

        } catch (NotificationNotFound e) {
            throw e;
        } catch (Exception e) {
            log.error("Lỗi khi xử lý thông báo cho targetId={}: {}", targetId, e.getMessage(), e);
            throw new RuntimeException("Không thể thêm thông báo", e);
        }

        String senderName = userService.findUserById(notification.getSenderId()).getFullName();
        notificationEventProducer.publish(
                new NotificationCreateEvent(tokensByUser, notification.getType(), senderName)
        );
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

}
