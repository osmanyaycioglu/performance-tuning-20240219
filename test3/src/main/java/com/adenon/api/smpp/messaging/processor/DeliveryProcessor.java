package com.adenon.api.smpp.messaging.processor;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.core.ResponseHandler;
import com.adenon.api.smpp.sdk.IMessage;
import com.adenon.api.smpp.sdk.MessageInformation;


public class DeliveryProcessor extends ResponseHandler {


    public DeliveryProcessor() {
        this.createHandler(1);
    }

    @Override
    public void fillMessageBody(final ByteBuffer buffer,
                                final int index,
                                final byte[] concatHeader) throws Exception {
        buffer.put((byte) 0);
        return;

    }

    @Override
    public MessageInformation getMessageInformation(final IMessage messageDescriptor) {
        return null;
    }

}
