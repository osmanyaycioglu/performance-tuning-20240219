package com.adenon.api.smpp.sdk;


public enum ENumberPlanIndicator {
    UNKNOWN(0x00),
    ISDN(0x01),
    DATA(0x03),
    TELEX(0x04),
    LAN_MOBILE(0x06),
    NATIONAL(0x08),
    PRIVATE(0x09),
    ERMES(0x0A),
    INTERNET_IP(0x0D),
    WAP_CLIENT_ID(0x12);


    private final int value;

    private ENumberPlanIndicator(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ENumberPlanIndicator getNpi(final int val) {
        switch (val) {
            case 0:
                return UNKNOWN;
            case 1:
                return ISDN;
            case 3:
                return DATA;
            case 4:
                return TELEX;
            case 6:
                return LAN_MOBILE;
            case 8:
                return NATIONAL;
            case 9:
                return PRIVATE;
            case 10:
                return ERMES;
            case 13:
                return INTERNET_IP;
            case 18:
                return WAP_CLIENT_ID;
            default:
                return UNKNOWN;
        }
    }

}
