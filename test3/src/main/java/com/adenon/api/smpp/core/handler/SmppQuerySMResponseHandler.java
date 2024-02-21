package com.adenon.api.smpp.core.handler;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.common.Smpp34ErrorCodes;
import com.adenon.api.smpp.core.SmppIOReactor;
import com.adenon.api.smpp.core.buffer.BufferBean;
import com.adenon.api.smpp.message.MessageHeader;
import com.adenon.api.smpp.message.Smpp34QuerySM;
import com.adenon.api.smpp.message.Smpp34QuerySMResponse;
import com.adenon.api.smpp.sdk.ESendResult;


public class SmppQuerySMResponseHandler {


    public void handle(final MessageHeader smpp34Header,
                       final SmppIOReactor smppIOReactor,
                       final ByteBuffer byteBuffer) throws Exception {

        final Smpp34QuerySMResponse smpp34QuerySMResponse = new Smpp34QuerySMResponse(smpp34Header.getSequenceNo());

        BufferBean bufferBean;
        smpp34QuerySMResponse.parseMessage(byteBuffer);
        bufferBean = smppIOReactor.getResponseBuffer().findItem(smpp34Header.getSequenceNo());
        if (bufferBean != null) {

            Smpp34QuerySM waitingQuerySm = null;
            try {
                waitingQuerySm = (Smpp34QuerySM) bufferBean.getWaitingObject();
            } catch (final Exception e) {
                smppIOReactor.getLogger().error("Smpp34QuerySMResponseHandler", "handle", 0, null, " : Error : " + e.getMessage(), e);
                return;
            } finally {
                bufferBean.release();
            }

            if (waitingQuerySm == null) {
                smppIOReactor.getLogger().error("Smpp34QuerySMResponseHandler", "handle", 0, null, " : Error : QuerySM should not have been null!!");
                return;
            }

            if (smppIOReactor.getConnectionInformation().getConnectionState().isSuspended()) {
                final int freeItemsCount = smppIOReactor.getResponseBuffer().getFreeItemCount();
                if (freeItemsCount > (smppIOReactor.getResponseBuffer().getBufferSize() / 2)) {
                    if (smppIOReactor.getLogger().isInfoEnabled()) {
                        smppIOReactor.getLogger().info("Smpp34QuerySMResponseHandler", "handle", 0, smppIOReactor.getLabel(), "Restarting... ");
                    }
                    smppIOReactor.getConnectionInformation().getConnectionState().idle();
                }
            }

            final int errorNo = smpp34Header.getCommandStatus();
            if (errorNo == Smpp34ErrorCodes.ERROR_CODE_ROK) {
                smppIOReactor.getStatisticCollector().increaseTotalReceivedSuccessfullQuerySMCount();
                smpp34QuerySMResponse.setSendResult(ESendResult.SUCCESS);
            } else {
                smppIOReactor.getStatisticCollector().increaseTotalReceivedFailedQuerySMCount();
                smpp34QuerySMResponse.setSendResult(ESendResult.FATAL_ERROR);
            }
            smppIOReactor.getSmppCallback().queryResult(smppIOReactor.getConnectionInformation(), smpp34QuerySMResponse);
        } else {
            smppIOReactor.getLogger().error("Smpp34QuerySMResponseHandler",
                                            "handle",
                                            0,
                                            smppIOReactor.getLabel(),
                                            "  Sequence is absent : " + smpp34Header.getSequenceNo());
        }
    }

}
