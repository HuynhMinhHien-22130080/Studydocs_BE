package com.mobile.studydocs.event;

public interface EventConsumer<T> {
    void consume(T event);
}
