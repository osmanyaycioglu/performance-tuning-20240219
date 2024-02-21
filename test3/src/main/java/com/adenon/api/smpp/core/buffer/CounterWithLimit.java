package com.adenon.api.smpp.core.buffer;

public class CounterWithLimit {

    private final int limit;
    private int       counter;

    public CounterWithLimit(final int _limit) {
        this.limit = _limit;
    }

    public synchronized int increase() {
        this.counter++;
        if (this.counter > this.limit) {
            this.counter = this.limit;
            return -1;
        }
        return this.counter;
    }

    public synchronized int decrease() {
        this.counter--;
        if (this.counter < 0) {
            this.counter = 0;
        }
        return this.counter;
    }

    public int getCounter() {
        return this.counter;
    }

    public void reset() {
        this.counter = 0;
    }

}
