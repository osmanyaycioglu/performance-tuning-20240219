package com.adenon.api.smpp.core.handler;

import com.adenon.api.smpp.core.SmppIOReactor;
import com.adenon.api.smpp.message.MessageHeader;
import com.adenon.api.smpp.sdk.AlarmCode;


public class SmppBindHandler {

    public void handle(final MessageHeader smpp34Header,
                       final SmppIOReactor ioReactor) {

        if (ioReactor.getLogger().isDebugEnabled()) {
            ioReactor.getLogger().debug("SmppMessageHandler",
                                        "handleMsg",
                                        smpp34Header.getSequenceNo(),
                                        ioReactor.getLabel(),
                                        " Bind response recieved : " + smpp34Header.getCommandID() + " . Handling binding process.");
        }
        final int resultCode = smpp34Header.getCommandStatus();
        if (resultCode == 0) {
            ioReactor.getBinded().set(true);
            if (ioReactor.getLogger().isInfoEnabled()) {
                ioReactor.getLogger().info("SmppBindHandler",
                                           "handle",
                                           smpp34Header.getSequenceNo(),
                                           ioReactor.getLabel(),
                                           "Bind operation is successfull to Host : " + ioReactor.getLabel());
            }
            ioReactor.getSmppCallback().binded(ioReactor.getConnectionInformation());
            ioReactor.getConnectionInformation().getConnectionState().idle();
        } else {
            try {
                if (resultCode == 15) {
                    ioReactor.getSmppCallback().alarm(ioReactor.getConnectionInformation(), AlarmCode.BindFailed, " User or Password is wrong");
                    ioReactor.getLogger().error("SmppIOReactor", "handleMsg", 0, ioReactor.getLabel(), " Bind failed : User or Password is wrong.");
                    ioReactor.closeConnection("Bind failed : User or Password is wrong.");
                } else if (resultCode == 5) {
                    ioReactor.getSmppCallback().alarm(ioReactor.getConnectionInformation(), AlarmCode.UserAlreadyBinded, " User has already  binded to Host");
                    ioReactor.getLogger().error("SmppIOReactor", "handleMsg", 0, ioReactor.getLabel(), "User has already  binded to Host.");
                    ioReactor.closeConnection("Bind failed : User has already  binded to Host.");
                } else {
                    ioReactor.getSmppCallback().alarm(ioReactor.getConnectionInformation(),
                                                      AlarmCode.UnknownBindError,
                                                      "Bind operation failed --> Unknown reason .");
                    ioReactor.getLogger().error("SmppIOReactor",
                                                "handleMsg",
                                                0,
                                                null,
                                                " : " + ioReactor.getLabel() + " Bind operation failed --> Unknown reason.");
                    ioReactor.closeConnection("Bind problem");
                }
            } catch (final Exception e) {
                ioReactor.getLogger().error("SmppIOReactor", "handleMsg", 0, null, " : Error : " + e.getMessage(), e);
            } finally {
                ioReactor.closeConnection("Bind failed so closing connection.");
            }
        }
    }
}
