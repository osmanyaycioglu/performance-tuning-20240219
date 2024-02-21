package com.adenon.api.smpp.common;

public class IndexCounter {

    private int index;

    public IndexCounter() {
    }

    public synchronized int increase(final int max) {
        this.index++;
        if (this.index > (max - 1)) {
            this.index = 0;
        }
        return this.index;
    }

    public int getIndex() {
        return this.index;
    }
}
