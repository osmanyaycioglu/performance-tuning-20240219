package com.adenon.api.smpp.core.handler;

import com.adenon.api.smpp.core.IIOReactor;
import com.adenon.api.smpp.core.buffer.BufferBean;
import com.adenon.api.smpp.message.MessageHeader;


public class SmppNackHandler {

    public void handle(final MessageHeader smpp34Header,
                       final IIOReactor smppIOReactor) {
        final BufferBean bufferBean = smppIOReactor.getResponseBuffer().findItem(smpp34Header.getSequenceNo());
        try {
            if (smppIOReactor.getLogger().isDebugEnabled()) {
                smppIOReactor.getLogger().debug("SmppNackHandler", "handle", 0, smppIOReactor.getLabel(), "General Nack Received");
            }
            smppIOReactor.getLogger().error("SmppNackHandler", "handle", 0, smppIOReactor.getLabel(), "General nack on : " + smpp34Header.getSequenceNo());
            if (bufferBean != null) {
                smppIOReactor.handleNack(bufferBean, smpp34Header);
            }
        } catch (final Exception e) {
            smppIOReactor.getLogger().error("SmppNackHandler", "handle", 0, smppIOReactor.getLabel(), " Error : " + e.getMessage(), e);
        } finally {
            if (bufferBean != null) {
                bufferBean.release();
            }
        }


    }

}
