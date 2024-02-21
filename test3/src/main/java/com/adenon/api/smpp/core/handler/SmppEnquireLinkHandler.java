package com.adenon.api.smpp.core.handler;

import com.adenon.api.smpp.core.IIOReactor;
import com.adenon.api.smpp.message.MessageHeader;


public class SmppEnquireLinkHandler {

    public void handle(final MessageHeader smpp34Header,
                       final IIOReactor smppIOReactor) throws Exception {
        smppIOReactor.sendAliveRes(smpp34Header.getSequenceNo());

    }

}
