package com.adenon.api.smpp.core;

import com.adenon.api.smpp.messaging.processor.IMessageHandler;
import com.adenon.api.smpp.messaging.processor.IMessageProcessor;


public abstract class ResponseHandler implements IMessageProcessor {

    private IMessageHandler messageHandler;


    public ResponseHandler() {
    }

    public void createHandler(final int size) {
        if (size > 1) {
            this.messageHandler = new ConcatMessageHandler(size);
        } else {
            this.messageHandler = new SingleMessageHandler();
        }
    }


    @Override
    public boolean responseReceived(final int sequenceNumber,
                                    final String messageIdentifier) {
        return this.messageHandler.responseReceived(sequenceNumber, messageIdentifier);
    }

    @Override
    public void addSequence(final int msgIndex,
                            final int sequenceNumber) {
        this.messageHandler.addSequence(msgIndex, sequenceNumber);
    }

    @Override
    public int getMessagePartCount() {
        return this.messageHandler.getMessagePartCount();
    }

    @Override
    public void errorReceived() {
        this.messageHandler.errorReceived();
    }

    @Override
    public boolean isLastSegment(final int sequenceNumber) {
        return this.messageHandler.isLastSegment(sequenceNumber);
    }

    @Override
    public String getMessageIdentifier() {
        return this.messageHandler.getMessageIdentifier();
    }


}
