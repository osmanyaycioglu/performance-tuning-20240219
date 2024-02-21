package com.adenon.api.smpp.sdk;


public enum SIActionType implements IActionType {

    SignalNone(5),
    SignalLow(6),
    SignalMedium(7),
    SignalHigh(8),
    Delete(9);

    private int value;

    private SIActionType(final int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return this.value;
    }
}
