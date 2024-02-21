package com.adenon.api.smpp.messaging.processor;


public interface IMessageHandler {

    public boolean responseReceived(int sequenceNumber,
                                    String messageIdentifier);

    public void addSequence(int msgIndex,
                            int sequenceNumber);

    public void errorReceived();

    public boolean isLastSegment(int sequenceNumber);

    public String getMessageIdentifier();

    public int getMessagePartCount();


}
