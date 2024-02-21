package com.adenon.api.smpp.message;

public class OptionalParameter {

    private int    optionalParameterTag;
    private byte[] optionalParameterData;

    public OptionalParameter() {
    }

    public int getOptionalParameterTag() {
        return this.optionalParameterTag;
    }

    public OptionalParameter setOptionalParameterTag(final int optionalParameterTag) {
        this.optionalParameterTag = optionalParameterTag;
        return this;
    }

    public byte[] getOptionalParameterData() {
        return this.optionalParameterData;
    }

    public OptionalParameter setOptionalParameterData(final byte[] optionalParameterData) {
        this.optionalParameterData = optionalParameterData;
        return this;
    }

}
