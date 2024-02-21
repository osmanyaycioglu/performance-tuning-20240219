package com.adenon.api.smpp.message;

import com.adenon.api.smpp.sdk.AddressDescriptor;

public class UnsuccessfulSME extends AddressDescriptor {

    private int errorCode;

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "Unsuccessful SME [number="
               + this.getNumber()
               + ", ton="
               + this.getTon()
               + ", npi="
               + this.getNpi()
               + ", error-code="
               + this.getErrorCode()
               + "]";
    }
}
