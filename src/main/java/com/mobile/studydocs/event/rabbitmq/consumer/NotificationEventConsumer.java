package com.mobile.studydocs.event.rabbitmq.consumer;

import com.mobile.studydocs.event.core.EventConsumer;
import com.mobile.studydocs.event.model.NotificationCreateEvent;
import com.mobile.studydocs.model.message.NotificationMessage;
import com.mobile.studydocs.model.message.NotificationMessageFactory;
import com.mobile.studydocs.service.FirebaseNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@RequiredArgsConstructor
public class NotificationEventConsumer implements EventConsumer<NotificationCreateEvent> {
    private final FirebaseNotificationService firebaseNotificationService;

    @Override
    @RabbitListener(queues = "notification-queue")
    public void consume(NotificationCreateEvent event) {
        NotificationMessage notificationMessage = NotificationMessageFactory.createMessage(event.type(), event.senderName());
        firebaseNotificationService.sendNotification(event.receiverTokens(), notificationMessage);
    }
}
