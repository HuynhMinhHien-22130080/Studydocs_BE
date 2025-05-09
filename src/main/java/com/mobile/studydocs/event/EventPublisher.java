package com.mobile.studydocs.event;

public interface EventPublisher<T> {
    void publish(T event);
}
