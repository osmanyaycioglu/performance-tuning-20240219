package com.adenon.api.smpp.sdk;

public enum SmppConnectionType {
    BOTH(0),
    WRITE(1),
    READ(2);

    private int value;

    private SmppConnectionType(final int val) {
        this.value = val;
    }

    public int getValue() {
        return this.value;
    }

    public static SmppConnectionType getLogType(final int val) {
        switch (val) {
            case 0:
                return BOTH;
            case 1:
                return WRITE;
            case 2:
                return READ;
            default:
                return BOTH;
        }
    }


}
