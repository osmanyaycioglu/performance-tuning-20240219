package com.adenon.api.smpp.core.handler;

import com.adenon.api.smpp.core.SmppIOReactor;
import com.adenon.api.smpp.message.MessageHeader;
import com.adenon.api.smpp.sdk.AlarmCode;


public class SmppUnbindResponseHandler {

    /**
     * @author mustafa.sinikci
     * 
     * @param smpp34Header
     * @param ioReactor
     * 
     *            if unbind response is ok set connection state as stopped set connection as shutdown
     * 
     *            else set alarm
     * 
     */
    public void handle(final MessageHeader smpp34Header,
                       final SmppIOReactor ioReactor) {

        if (ioReactor.getLogger().isDebugEnabled()) {
            ioReactor.getLogger().debug("SmppMessageHandler",
                                        "handleMsg",
                                        smpp34Header.getSequenceNo(),
                                        ioReactor.getLabel(),
                                        " Unbind response recieved : " + smpp34Header.getCommandID() + " . Handling unbinding process.");
        }
        final int resultCode = smpp34Header.getCommandStatus();
        if (resultCode == 0) {
            ioReactor.getBinded().set(false);
            if (ioReactor.getLogger().isInfoEnabled()) {
                ioReactor.getLogger().info("SmppUnbindHandler",
                                           "handle",
                                           smpp34Header.getSequenceNo(),
                                           ioReactor.getLabel(),
                                           "Unbind operation is successfull from Host : " + ioReactor.getLabel());
            }

            ioReactor.getConnectionInformation().getConnectionState().stopped();
            ioReactor.setShutdown(true);
            ioReactor.getSmppCallback().unbinded(ioReactor.getConnectionInformation());

        } else {

            ioReactor.getSmppCallback().alarm(ioReactor.getConnectionInformation(), AlarmCode.UnknownUnbindError, "Unbind operation failed .");

            ioReactor.getLogger().error("SmppIOReactor", "handleMsg", 0, null, " : " + ioReactor.getLabel() + " Unbind operation failed.");
        }

    }

}
