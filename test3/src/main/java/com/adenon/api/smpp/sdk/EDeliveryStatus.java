package com.adenon.api.smpp.sdk;

public enum EDeliveryStatus {
    // ENROUTE 1 The message is in enroute state.
    // DELIVERED 2 Message is delivered to destination
    // EXPIRED 3 Message validity period has expired.
    // DELETED 4 Message has been deleted.
    // UNDELIVERABLE 5 Message is undeliverable
    // ACCEPTED 6 Message is in accepted state (i.e. has been manually read on behalf of the subscriber by customer service)
    // UNKNOWN 7 Message is in invalid state
    // REJECTED 8 Message is in a rejected state
    ENROUTE(1),
    DELIVERED(2),
    EXPIRED(3),
    DELETED(4),
    UNDELIVERABLE(5),
    ACCEPTED(6),
    UNKNOWN(7),
    REJECTED(8);

    private final int value;

    private EDeliveryStatus(final int value) {
        this.value = value;

    }

    public int getValue() {
        return this.value;
    }

    public static final EDeliveryStatus getDeliveryStatus(final int val) {
        switch (val) {
            case 1:
                return ENROUTE;
            case 2:
                return DELIVERED;
            case 3:
                return EXPIRED;
            case 4:
                return DELETED;
            case 5:
                return UNDELIVERABLE;
            case 6:
                return ACCEPTED;
            case 7:
                return UNKNOWN;
            case 8:
                return REJECTED;
            default:
                return UNKNOWN;
        }
    }

}
