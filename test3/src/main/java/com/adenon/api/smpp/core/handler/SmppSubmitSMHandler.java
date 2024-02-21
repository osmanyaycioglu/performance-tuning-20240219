package com.adenon.api.smpp.core.handler;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.core.SmppIOReactor;
import com.adenon.api.smpp.message.MessageHeader;
import com.adenon.api.smpp.message.SubmitSMMessage;


public class SmppSubmitSMHandler {

    public void handle(final MessageHeader smpp34Header,
                       final SmppIOReactor smppIOReactor,
                       final ByteBuffer byteBuffer) throws Exception {
        final SubmitSMMessage submitSM = new SubmitSMMessage(smppIOReactor.getLogger(), smpp34Header.getSequenceNo(), smppIOReactor.getLabel());
        submitSM.parseMessage(byteBuffer);
        if (smppIOReactor.getLogger().isDebugEnabled()) {
            smppIOReactor.getLogger().debug("SmppSubmitSMHandler",
                                            "handle",
                                            0,
                                            smppIOReactor.getLabel(),
                                            " Source : " + submitSM.getSourceAddress().toString() + " Dest : " + submitSM.getDestinationAddress().toString());
        }
        smppIOReactor.sendSubmitSMResponse(smpp34Header.getSequenceNo(), byteBuffer, 0);
        smppIOReactor.getStatisticCollector().increaseTotalReceivedSubmitSMCount();
        smppIOReactor.getSmppCallback().submitSMReceived(smppIOReactor.getConnectionInformation(), submitSM);

    }

}
