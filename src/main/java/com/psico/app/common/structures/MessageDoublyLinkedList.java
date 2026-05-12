package com.psico.app.common.structures;

import java.util.NoSuchElementException;

public class MessageDoublyLinkedList<T> {

    private Node<T> head;
    private Node<T> tail;
    private Node<T> current;

    public void add(T value) {
        Node<T> node = new Node<>(value);
        if (head == null) {
            head = tail = current = node;
        } else {
            tail.next = node;
            node.previous = tail;
            tail = node;
        }
    }

    public T current() {
        if (current == null) {
            throw new NoSuchElementException("No messages available");
        }
        return current.value;
    }

    public T previous() {
        if (current == null || current.previous == null) {
            throw new NoSuchElementException("No previous message");
        }
        current = current.previous;
        return current.value;
    }

    public T next() {
        if (current == null || current.next == null) {
            throw new NoSuchElementException("No next message");
        }
        current = current.next;
        return current.value;
    }

    public boolean hasPrevious() {
        return current != null && current.previous != null;
    }

    public boolean hasNext() {
        return current != null && current.next != null;
    }

    private static final class Node<T> {
        private final T value;
        private Node<T> previous;
        private Node<T> next;

        private Node(T value) {
            this.value = value;
        }
    }
}
