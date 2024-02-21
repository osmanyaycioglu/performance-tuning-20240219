package com.adenon.library.common.utils;


public class Concurrent<T> {

    private T value;

    public Concurrent(final T value) {
        this.value = value;

    }

    public synchronized T getValue() {
        return this.value;
    }

    public synchronized void setValue(final T newValue) {
        this.value = newValue;
    }

}
