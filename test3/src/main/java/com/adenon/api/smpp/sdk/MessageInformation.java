package com.adenon.api.smpp.sdk;


public class MessageInformation {

    private int messageCount;
    private int byteCount;


    public int getMessageCount() {
        return this.messageCount;
    }

    public void setMessageCount(final int messageCount) {
        this.messageCount = messageCount;
    }

    public int getByteCount() {
        return this.byteCount;
    }

    public void setByteCount(final int byteCount) {
        this.byteCount = byteCount;
    }

    @Override
    public String toString() {
        return "<MessageInformation> messageCount : " + this.messageCount + " , byteCount : " + this.byteCount;
    }


}
