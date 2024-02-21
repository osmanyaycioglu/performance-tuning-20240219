package com.adenon.api.smpp.core;


public class ResponseBean {

    private int     sequenceNumber;
    private boolean responseReceived;
    private String  messageIdentifier;

    public int getSequenceNumber() {
        return this.sequenceNumber;
    }

    public void setSequenceNumber(final int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public boolean isResponseReceived() {
        return this.responseReceived;
    }

    public void setResponseReceived(final boolean responseReceived) {
        this.responseReceived = responseReceived;
    }

    public String getMessageIdentifier() {
        return this.messageIdentifier;
    }

    public void setMessageIdentifier(final String messageIdentifier) {
        this.messageIdentifier = messageIdentifier;
    }
}
