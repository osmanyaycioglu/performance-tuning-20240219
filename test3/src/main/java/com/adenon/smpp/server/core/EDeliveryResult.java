package com.adenon.smpp.server.core;


public enum EDeliveryResult {
    DeliveredSuccesfully(0, "Delivered succeessfully"),
    DeliveryFailed(1, "Delivery Failed"),
    RetryDelivery(2, "Please retry delivery");

    private final int    value;
    private final String description;

    private EDeliveryResult(final int value,
                            final String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return this.value;
    }


    public String getDescription() {
        return this.description;
    }

}
