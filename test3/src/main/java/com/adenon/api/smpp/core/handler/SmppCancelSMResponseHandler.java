package com.adenon.api.smpp.core.handler;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.core.SmppIOReactor;
import com.adenon.api.smpp.core.buffer.BufferBean;
import com.adenon.api.smpp.message.MessageHeader;
import com.adenon.api.smpp.message.Smpp34CancelSM;


public class SmppCancelSMResponseHandler {

    public void handle(final MessageHeader smpp34Header,
                       final SmppIOReactor smppIOReactor,
                       final ByteBuffer byteBuffer) throws Exception {

        final BufferBean bufferBean = smppIOReactor.getResponseBuffer().findItem(smpp34Header.getSequenceNo());
        if (bufferBean != null) {
            Smpp34CancelSM cancelSM;
            try {
                cancelSM = (Smpp34CancelSM) bufferBean.getWaitingObject();
            } catch (final Exception e) {
                smppIOReactor.getLogger().error("SmppCancelSMResponseHandler", "handle", 0, smppIOReactor.getLabel(), " : Error : " + e.getMessage(), e);
                return;
            } finally {
                bufferBean.release();
            }
            if (smppIOReactor.getConnectionInformation().getConnectionState().isSuspended()) {
                final int freeItemsCount = smppIOReactor.getResponseBuffer().getFreeItemCount();
                if (freeItemsCount > (smppIOReactor.getResponseBuffer().getBufferSize() / 2)) {
                    if (smppIOReactor.getLogger().isInfoEnabled()) {
                        smppIOReactor.getLogger().info("SmppCancelSMResponseHandler", "handle", 0, smppIOReactor.getLabel(), "Restarting... ");
                    }
                    smppIOReactor.getConnectionInformation().getConnectionState().idle();
                }
            }
            final int errorNo = smpp34Header.getCommandStatus();
            smppIOReactor.getSmppCallback().cancelResult(smppIOReactor.getConnectionInformation(),
                                                         smpp34Header.getSequenceNo(),
                                                         errorNo,
                                                         cancelSM.getParamMessageId());
        } else {
            smppIOReactor.getLogger().error("SmppCancelSMResponseHandler",
                                            "handle",
                                            0,
                                            smppIOReactor.getLabel(),
                                            "Sequence not available : " + smpp34Header.getSequenceNo());
        }
    }
}
