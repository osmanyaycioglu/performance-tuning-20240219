package com.adenon.api.smpp.core.handler;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.common.Smpp34ErrorCodes;
import com.adenon.api.smpp.core.SmppIOReactor;
import com.adenon.api.smpp.message.DeliverSMMessage;
import com.adenon.api.smpp.message.MessageHeader;


public class SmppDeliverSMHandler {

    public void handle(final MessageHeader smpp34Header,
                       final SmppIOReactor smppIOReactor,
                       final ByteBuffer byteBuffer) throws Exception {

        int errCode = 0;

        final DeliverSMMessage deliverSM = new DeliverSMMessage(smppIOReactor.getLogger(), -1, smppIOReactor.getLabel());
        deliverSM.parseMessage(byteBuffer);
        final boolean deliveryRecp = ((deliverSM.getParamESMClass() & 0x00000004) == 0x00000004);
        if (smppIOReactor.getLogger().isDebugEnabled()) {
            smppIOReactor.getLogger().debug("SmppMessageHandler",
                                            "handle",
                                            0,
                                            smppIOReactor.getLabel(),
                                            " Received : " + deliverSM.toString() + " delivery : " + deliveryRecp);
        }
        if (deliveryRecp) {
            smppIOReactor.getStatisticCollector().increaseTotalReceivedDeliveryCount();
            if (!smppIOReactor.getSmppCallback().deliveryReceived(smppIOReactor.getConnectionInformation(), deliverSM, deliverSM.getOpParamMessageState())) {
                errCode = Smpp34ErrorCodes.ERROR_CODE_RMSGQFUL;
                smppIOReactor.getLogger().error("SmppDeliverSMHandler",
                                                "handle",
                                                0,
                                                smppIOReactor.getLabel(),
                                                "Callback refuse to get this delivery . Sequence : "
                                                        + smpp34Header.getSequenceNo()
                                                        + " DeliverSM : "
                                                        + deliverSM.toString());
            }
        } else {
            smppIOReactor.getStatisticCollector().increaseTotalReceivedDeliverSMCount();
            if (!smppIOReactor.getSmppCallback().deliverSMReceived(smppIOReactor.getConnectionInformation(), deliverSM)) {
                errCode = Smpp34ErrorCodes.ERROR_CODE_RMSGQFUL;
                smppIOReactor.getLogger().error("SmppDeliverSMHandler",
                                                "handle",
                                                0,
                                                smppIOReactor.getLabel(),
                                                "Callback returned false!! Sending Queue Full . Sequence : "
                                                        + smpp34Header.getSequenceNo()
                                                        + " DeliverSM : "
                                                        + deliverSM.toString());
            }
        }
        smppIOReactor.sendDeliverSMResponse(smpp34Header.getSequenceNo(), byteBuffer, errCode);

    }

}
