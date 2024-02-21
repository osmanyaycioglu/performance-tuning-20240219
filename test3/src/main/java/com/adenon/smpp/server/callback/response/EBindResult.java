package com.adenon.smpp.server.callback.response;

import com.adenon.api.smpp.common.Smpp34ErrorCodes;


public enum EBindResult {
    BindSuccess(Smpp34ErrorCodes.ERROR_CODE_ROK, "Bind success"),
    AlreadyBinded(Smpp34ErrorCodes.ERROR_CODE_RALYBND, "Already binded"),
    InvalidInterface(Smpp34ErrorCodes.ERROR_CODE_RSYSERR, "Interface mismatch"),
    InvalidLogin(Smpp34ErrorCodes.ERROR_CODE_RINVSYSID, "invalid login");

    private final int    value;
    private final String description;

    private EBindResult(final int value,
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
