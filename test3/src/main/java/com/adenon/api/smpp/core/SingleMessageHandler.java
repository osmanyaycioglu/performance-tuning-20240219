package com.adenon.api.smpp.core;

import com.adenon.api.smpp.messaging.processor.IMessageHandler;


public class SingleMessageHandler implements IMessageHandler {

    private int    sequenceNumber;
    private String messageIdentifier;

    @Override
    public boolean responseReceived(final int sequenceNumber,
                                    final String messageIdentifier) {
        if (this.sequenceNumber == sequenceNumber) {
            this.messageIdentifier = messageIdentifier;
            return true;
        }
        return false;
    }

    @Override
    public void addSequence(final int msgIndex,
                            final int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;

    }

    @Override
    public void errorReceived() {

    }

    @Override
    public boolean isLastSegment(final int sequenceNumber) {
        return true;
    }

    @Override
    public String getMessageIdentifier() {
        return this.messageIdentifier;
    }

    @Override
    public int getMessagePartCount() {
        return 1;
    }

}
