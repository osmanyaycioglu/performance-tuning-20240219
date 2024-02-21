package com.adenon.api.smpp.sdk;


public enum ETypeOfNumber {
    UNKNOWN(0x00),
    INTERNATIONAL(0x01),
    NATIONAL(0x02),
    NETWORK_SPECIFIC(0x03),
    SUBSCRIBER_NUMBER(0x04),
    ALPHANUMERIC(0x05),
    ABBREVIATED(0x06);

    private final int value;

    private ETypeOfNumber(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ETypeOfNumber getTon(final int val) {
        switch (val) {
            case 0:
                return UNKNOWN;
            case 1:
                return INTERNATIONAL;
            case 2:
                return NATIONAL;
            case 3:
                return NETWORK_SPECIFIC;
            case 4:
                return SUBSCRIBER_NUMBER;
            case 5:
                return ALPHANUMERIC;
            case 6:
                return ABBREVIATED;
            default:
                return UNKNOWN;
        }
    }
}
