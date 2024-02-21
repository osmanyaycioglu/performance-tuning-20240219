package com.adenon.api.smpp.core.buffer;

import com.adenon.api.smpp.message.MessageObject;

public class TimedoutObject {

    private MessageObject waitingObject;
    private int           sequence;

    public int getSequence() {
        return this.sequence;
    }

    public void setSequence(final int sequence) {
        this.sequence = sequence;
    }

    public MessageObject getWaitingObject() {
        return this.waitingObject;
    }

    public void setWaitingObject(final MessageObject waitingObject) {
        this.waitingObject = waitingObject;
    }
}
