package com.adenon.api.smpp.sdk;


public class ApiProperties {

    private int threadCount = 40;


    public ApiProperties() {
    }

    public int getThreadCount() {
        return this.threadCount;
    }

    public ApiProperties setThreadCount(final int threadCount) {
        this.threadCount = threadCount;
        return this;
    }
}
