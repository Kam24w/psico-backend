package com.psico.app.common.structures;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class CircularRecommendationList<T> {

    private final List<T> recommendations = new ArrayList<>();
    private int pointer = 0;

    public void add(T recommendation) {
        recommendations.add(recommendation);
    }

    public T next() {
        if (recommendations.isEmpty()) {
            throw new NoSuchElementException("No recommendations available");
        }
        T result = recommendations.get(pointer);
        pointer = (pointer + 1) % recommendations.size();
        return result;
    }

    public boolean isEmpty() {
        return recommendations.isEmpty();
    }
}
