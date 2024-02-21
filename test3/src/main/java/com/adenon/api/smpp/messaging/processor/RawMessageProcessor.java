package com.adenon.api.smpp.messaging.processor;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.core.ResponseHandler;
import com.adenon.api.smpp.sdk.IMessage;
import com.adenon.api.smpp.sdk.IRawMessages;
import com.adenon.api.smpp.sdk.MessageInformation;


public class RawMessageProcessor extends ResponseHandler {

    private final IRawMessages rawMessage;

    public RawMessageProcessor(final IRawMessages rawMessage) {
        this.rawMessage = rawMessage;
    }

    @Override
    public void fillMessageBody(final ByteBuffer buffer,
                                final int index,
                                final byte[] concatHeader) throws Exception {

    }

    @Override
    public MessageInformation getMessageInformation(final IMessage messageDescriptor) {
        final int messageCount = this.rawMessage.getMessageList().size();
        final MessageInformation messageInformation = new MessageInformation();
        messageInformation.setMessageCount(messageCount);
        return messageInformation;
    }

}
