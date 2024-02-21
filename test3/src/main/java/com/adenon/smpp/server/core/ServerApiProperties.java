package com.adenon.smpp.server.core;


public class ServerApiProperties {

    private int     threadCount    = 40;
    private int     port           = 5101;
    private int     windowSize     = 100;
    private int     maxThreadCount = 5;
    private boolean traceOn        = false;


    public ServerApiProperties() {
    }

    public int getThreadCount() {
        return this.threadCount;
    }

    public ServerApiProperties setThreadCount(final int threadCount) {
        this.threadCount = threadCount;
        return this;
    }

    public int getPort() {
        return this.port;
    }

    public ServerApiProperties setPort(final int port) {
        this.port = port;
        return this;
    }

    public int getWindowSize() {
        return this.windowSize;
    }

    public ServerApiProperties setWindowSize(final int windowSize) {
        this.windowSize = windowSize;
        return this;
    }

    public int getMaxThreadCount() {
        return this.maxThreadCount;
    }

    public ServerApiProperties setMaxThreadCount(final int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
        return this;
    }

    public boolean isTraceOn() {
        return this.traceOn;
    }

    public ServerApiProperties setTraceOn(final boolean traceOn) {
        this.traceOn = traceOn;
        return this;
    }
}
