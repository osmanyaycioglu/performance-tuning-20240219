package com.adenon.api.smpp.core.handler;

import com.adenon.api.smpp.core.SmppIOReactor;
import com.adenon.api.smpp.message.MessageHeader;

/**
 * @author mustafa.sinikci
 * 
 * @param smpp34Header
 * @param ioReactor
 * 
 *            send unbind response message with sequence number of unbind request to host then stop connection and shutdown the reactor
 * 
 */
public class SmppUnbindHandler {

    public void handle(final MessageHeader header,
                       final SmppIOReactor smppIOReactor) {


        if (smppIOReactor.getLogger().isDebugEnabled()) {
            smppIOReactor.getLogger().debug("SmppMessageHandler",
                                            "handleMsg",
                                            header.getSequenceNo(),
                                            smppIOReactor.getLabel(),
                                            " Unbind request recieved : " + header.getCommandID() + " . Handling unbinding process.");
        }

        smppIOReactor.sendUnbindResponse(header.getSequenceNo());

        smppIOReactor.getBinded().set(false);
        smppIOReactor.getConnectionInformation().getConnectionState().stopped();
        smppIOReactor.setShutdown(true);
        smppIOReactor.getSmppCallback().unbinded(smppIOReactor.getConnectionInformation());
    }
}
