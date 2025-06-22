package com.mobile.studydocs.event.rabbitmq.consumer;

import com.mobile.studydocs.dao.UserDao;
import com.mobile.studydocs.event.core.EventConsumer;
import com.mobile.studydocs.event.model.NotificationCreateEvent;
import com.mobile.studydocs.model.enums.NotificationType;
import com.mobile.studydocs.model.message.NotificationMessage;
import com.mobile.studydocs.model.message.NotificationMessageFactory;
import com.mobile.studydocs.service.FirebaseNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class NotificationEventConsumer implements EventConsumer<NotificationCreateEvent> {
    private final FirebaseNotificationService firebaseNotificationService;
    private final UserDao userDao;

    @Override
    @RabbitListener(queues = "notification-queue")
    public void consume(NotificationCreateEvent event) {
        NotificationMessage message = buildNotificationMessage(event.getType(), event.getSenderName());
        Map<String, List<String>> invalidTokensByUser =
                firebaseNotificationService.sendNotification(event.getReceiverTokens(), message);
        if (!invalidTokensByUser.isEmpty()) {
            removeInvalidTokens(invalidTokensByUser);
        }

    }

    private NotificationMessage buildNotificationMessage(NotificationType type, String senderName) {
        return NotificationMessageFactory.createMessage(type, senderName);
    }

    private void removeInvalidTokens(Map<String, List<String>> tokensByUser) {
        tokensByUser.forEach((userId, tokens) ->
                tokens.forEach(token -> userDao.removeFcmToken(userId, token))
        );
    }
}
