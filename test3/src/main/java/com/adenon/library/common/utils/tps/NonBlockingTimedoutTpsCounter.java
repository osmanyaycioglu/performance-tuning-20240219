package com.adenon.library.common.utils.tps;


public class NonBlockingTimedoutTpsCounter {

    private final int maxTps;
    private int       counter;
    private long      startTime;

    private boolean   timeoutInprogress = false;
    private int       tmpMaxTps;
    private long      timeout;
    private long      temporaryTpsStartDate;

    public NonBlockingTimedoutTpsCounter(final int maxTps) {
        this.maxTps = maxTps;
        this.startTime = System.currentTimeMillis();
    }

    public synchronized int increase() {
        this.counter++;
        if (this.timeoutInprogress) {
            if (System.currentTimeMillis() > (this.temporaryTpsStartDate + this.timeout)) {
                this.timeoutInprogress = false;
            }
        }

        if (this.timeoutInprogress) {
            if (this.counter > this.tmpMaxTps) {
                final long delta = (System.currentTimeMillis() - this.startTime);
                if (delta < 1000) {
                    return -1;
                } else {
                    this.startTime = System.currentTimeMillis();
                    this.counter = 1;
                    return this.counter;
                }
            } else {
                return this.counter;
            }
        } else {
            if (this.counter > this.maxTps) {
                final long delta = (System.currentTimeMillis() - this.startTime);
                if (delta < 1000) {
                    return -1;
                } else {
                    this.startTime = System.currentTimeMillis();
                    this.counter = 1;
                    return this.counter;
                }
            } else {
                return this.counter;
            }

        }
    }

    public void adjustTps(final int newMaxTps,
                          final long timeout) {
        this.tmpMaxTps = newMaxTps;
        this.timeout = timeout;
        this.temporaryTpsStartDate = System.currentTimeMillis();

    }

    public int getMaxTps() {
        if (this.timeoutInprogress) {
            return this.tmpMaxTps;
        } else {
            return this.maxTps;
        }

    }
}
