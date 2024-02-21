package com.adenon.api.smpp.sdk.objects;


public class ThrottleObject {

    private long throttlePeriod;
    private int  reduceTpsBy;

    public long getThrottlePeriod() {
        return this.throttlePeriod;
    }

    public void setThrottlePeriod(final long throttlePeriod) {
        this.throttlePeriod = throttlePeriod;
    }

    public int getReduceTpsBy() {
        return this.reduceTpsBy;
    }

    public void setReduceTpsBy(final int reduceTpsBy) {
        this.reduceTpsBy = reduceTpsBy;
    }

}
