package com.mobile.studydocs.event.rabbitmq.producer;

import com.mobile.studydocs.event.core.EventProducer;
import com.mobile.studydocs.event.model.NotificationCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotificationEventProducer implements EventProducer<NotificationCreateEvent> {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(NotificationCreateEvent event) {
        rabbitTemplate.convertAndSend("notification-exchange", "notification.create", event);
    }
}
