package com.psico.app.common.structures;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class AlertQueue<T> {

    private final Deque<T> queue = new ArrayDeque<>();

    public void enqueue(T alert) {
        queue.addLast(alert);
    }

    public T dequeue() {
        return queue.pollFirst();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public List<T> snapshot() {
        return List.copyOf(queue);
    }
}
