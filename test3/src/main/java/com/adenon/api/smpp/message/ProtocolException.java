package com.adenon.api.smpp.message;

public class ProtocolException extends Exception {

    private static final long serialVersionUID = -3292333042375894463L;

    public String             errorDescription;

    public ProtocolException(final String errorDescription) {
        this.errorDescription = errorDescription;
    }

    @Override
    public String getMessage() {
        return this.errorDescription;
    }
}
