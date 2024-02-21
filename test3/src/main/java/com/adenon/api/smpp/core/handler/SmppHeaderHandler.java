package com.adenon.api.smpp.core.handler;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.message.MessageHeader;


public class SmppHeaderHandler {

    public MessageHeader handle(final ByteBuffer byteBuffer) {
        final MessageHeader smpp34Header = new MessageHeader();
        smpp34Header.parse(byteBuffer);
        return smpp34Header;

    }
}
