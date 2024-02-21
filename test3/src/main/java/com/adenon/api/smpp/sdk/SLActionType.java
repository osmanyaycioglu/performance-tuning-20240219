package com.adenon.api.smpp.sdk;


public enum SLActionType implements IActionType {

    ExecuteLow(5),
    ExecuteHigh(6),
    Cache(7);

    private int value;

    private SLActionType(final int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return this.value;
    }
}
