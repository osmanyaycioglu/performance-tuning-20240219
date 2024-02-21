package com.adenon.smpp.server.callback.response;

import com.adenon.api.smpp.common.Smpp34ErrorCodes;


public enum ESubmitResult {
    submitSuccess(Smpp34ErrorCodes.ERROR_CODE_ROK, "Submit success"),
    QueueFull(Smpp34ErrorCodes.ERROR_CODE_RMSGQFUL, "Queue Full"),
    ThrottleTraffic(Smpp34ErrorCodes.ERROR_CODE_RTHROTTLED, "invalid login"),
    SystemError(Smpp34ErrorCodes.ERROR_CODE_RSYSERR, "system error"),
    NoEnoughCredits(Smpp34ErrorCodes.ERROR_CODE_NO_ENOUGH_CREDITS, "No enough credits"),
    DoNothing(-1, "Dont send ack");

    private final int    value;
    private final String description;

    private ESubmitResult(final int value,
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
