package com.mobile.studydocs.event.core;

public interface EventProducer<T> {
    void publish(T event);
}
