package com.adenon.api.smpp.sdk;


public enum ESendResult {
    SUCCESS(0),
    RETRY(1),
    ERROR(2),
    FATAL_ERROR(3);

    private final int value;

    private ESendResult(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}
