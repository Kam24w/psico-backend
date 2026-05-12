package com.psico.app.common.structures;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class EmotionHistoryStack<T> {

    private final Deque<T> stack = new ArrayDeque<>();
    private final int capacity;

    public EmotionHistoryStack(int capacity) {
        this.capacity = capacity;
    }

    public void push(T item) {
        if (stack.size() == capacity) {
            stack.removeLast();
        }
        stack.push(item);
    }

    public T pop() {
        return stack.pollFirst();
    }

    public T peek() {
        return stack.peekFirst();
    }

    public List<T> toList() {
        return List.copyOf(stack);
    }
}
