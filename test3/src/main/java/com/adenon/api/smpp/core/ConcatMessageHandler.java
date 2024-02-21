package com.adenon.api.smpp.core;

import com.adenon.api.smpp.messaging.processor.IMessageHandler;

public class ConcatMessageHandler implements IMessageHandler {

    private final ResponseBean[] parts;
    private int                  messagePartCount;

    public ConcatMessageHandler(final int messageCount) {
        this.messagePartCount = messageCount;

        this.parts = new ResponseBean[this.messagePartCount];
        for (int i = 0; i < this.messagePartCount; i++) {
            this.parts[i] = new ResponseBean();
        }
    }

    private void addSequenceImpl(final int msgIndex,
                                 final int sequenceNumber) {
        try {
            final ResponseBean responseBean = this.parts[msgIndex];
            if (responseBean != null) {
                responseBean.setSequenceNumber(sequenceNumber);
            }
        } catch (final Exception e) {
        }
    }

    private synchronized boolean responseReceivedImpl(final int sequenceNumber,
                                                      final String messageIdentifier) {
        boolean allResponseReceived = true;
        for (int i = 0; i < this.parts.length; i++) {
            final ResponseBean responseBean = this.parts[i];
            if (responseBean.getSequenceNumber() == sequenceNumber) {
                responseBean.setResponseReceived(true);
                responseBean.setMessageIdentifier(messageIdentifier);
            }
            allResponseReceived = allResponseReceived && responseBean.isResponseReceived();
            if (!allResponseReceived) {
                return false;
            }
        }
        // System.err.println("Sequence : " + sequenceNumber + " messageIdentifier : " + messageIdentifier + " allResponseReceived : "
        // + allResponseReceived);
        return allResponseReceived;
    }

    @Override
    public boolean responseReceived(final int sequenceNumber,
                                    final String messageIdentifier) {
        return this.responseReceivedImpl(sequenceNumber, messageIdentifier);
    }

    @Override
    public void addSequence(final int msgIndex,
                            final int sequenceNumber) {
        this.addSequenceImpl(msgIndex, sequenceNumber);
    }

    @Override
    public int getMessagePartCount() {
        return this.messagePartCount;
    }

    @Override
    public void errorReceived() {

    }

    @Override
    public boolean isLastSegment(final int sequenceNumber) {
        for (int i = 0; i < this.parts.length; i++) {
            final ResponseBean responseBean = this.parts[i];
            if (responseBean.getSequenceNumber() == sequenceNumber) {
                if (i == (this.parts.length - 1)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getMessageIdentifier() {
        final ResponseBean responseBean = this.parts[this.parts.length - 1];
        return responseBean.getMessageIdentifier();
    }

    public void setMessagePartCount(final int messagePartCount) {
        this.messagePartCount = messagePartCount;
    }

}
