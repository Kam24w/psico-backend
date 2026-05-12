package com.psico.app.common.structures;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class EmotionProcessingQueue<T> {

    private final Deque<T> queue = new ArrayDeque<>();

    public void enqueue(T item) {
        queue.addLast(item);
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
