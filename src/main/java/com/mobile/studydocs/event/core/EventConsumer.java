package com.mobile.studydocs.event.core;

public interface EventConsumer<T> {
    void consume(T event);
}
