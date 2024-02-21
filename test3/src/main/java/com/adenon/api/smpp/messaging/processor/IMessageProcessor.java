package com.adenon.api.smpp.messaging.processor;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.sdk.IMessage;
import com.adenon.api.smpp.sdk.MessageInformation;


public interface IMessageProcessor extends IMessageHandler {

    public void fillMessageBody(ByteBuffer buffer,
                                int index,
                                byte[] concatHeader) throws Exception;

    public MessageInformation getMessageInformation(IMessage messageDescriptor);

}
