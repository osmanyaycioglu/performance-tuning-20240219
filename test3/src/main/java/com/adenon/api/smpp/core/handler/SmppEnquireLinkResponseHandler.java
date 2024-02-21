package com.adenon.api.smpp.core.handler;

import com.adenon.api.smpp.core.IIOReactor;
import com.adenon.api.smpp.message.MessageHeader;


public class SmppEnquireLinkResponseHandler {

    public void handle(final MessageHeader smpp34Header,
                       final IIOReactor smppIOReactor) {
        smppIOReactor.getConnectionController().aliveResponse(smpp34Header.getSequenceNo());

    }

}
