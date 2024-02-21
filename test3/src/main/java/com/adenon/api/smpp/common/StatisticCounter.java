package com.adenon.api.smpp.common;

import java.util.concurrent.atomic.AtomicLong;

public class StatisticCounter {

    private final AtomicLong currentValue;
    private long             lastGivenValue;

    public StatisticCounter(final long _value) {
        this.currentValue = new AtomicLong(_value);
        this.lastGivenValue = _value;
    }

    public void increaseCounter() {
        this.currentValue.incrementAndGet();
    }

    public void decreaseCounter() {
        final long val = this.currentValue.decrementAndGet();
        if (val < 0) {
            this.currentValue.set(0);
        }
    }

    public void increaseCounter(final long value) {
        this.currentValue.addAndGet(value);
    }

    public long getCounter() {
        return this.currentValue.get();
    }

    public void resetCounter() {
        this.currentValue.set(0);
    }

    public long getAndResetCounter() {
        final long temp = this.currentValue.getAndSet(0);
        return temp;
    }

    public void setCounter(final int _value) {
        this.currentValue.set(_value);
    }

    @Override
    public String toString() {
        return "" + this.currentValue.get();
    }

    public int getLastValue() {
        final long retVal = this.lastGivenValue;
        this.lastGivenValue = this.currentValue.get();
        final int ret = (int) (this.lastGivenValue - retVal);
        return ret;
    }
}
