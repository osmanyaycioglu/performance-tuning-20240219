package com.adenon.api.smpp.sdk.objects;


public class QueueFullObject {

    private long suspendPeriod;

    public long getSuspendPeriod() {
        return this.suspendPeriod;
    }

    public void setSuspendPeriod(final long suspendPeriod) {
        this.suspendPeriod = suspendPeriod;
    }
}
