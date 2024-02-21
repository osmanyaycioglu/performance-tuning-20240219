package com.adenon.api.smpp.common;


public enum EState {
    STOPPED(0),
    SUSPENED(1),
    IDLE(2);

    private int enumIntVal;

    private EState(final int enumIntVal) {
        this.enumIntVal = enumIntVal;
    }

    public int getEnumIntVal() {
        return this.enumIntVal;
    }

}
