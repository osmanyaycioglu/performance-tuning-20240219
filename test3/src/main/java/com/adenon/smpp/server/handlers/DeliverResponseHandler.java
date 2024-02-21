package com.adenon.smpp.server.handlers;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.common.Smpp34ErrorCodes;
import com.adenon.api.smpp.common.SmppApiException;
import com.adenon.api.smpp.core.buffer.BufferBean;
import com.adenon.api.smpp.message.DeliverSMMessage;
import com.adenon.api.smpp.message.MessageHeader;
import com.adenon.api.smpp.messaging.processor.IMessageProcessor;
import com.adenon.api.smpp.sdk.ESendResult;
import com.adenon.smpp.server.core.ServerIOReactor;


public class DeliverResponseHandler {

    public void handle(final MessageHeader header,
                       final ByteBuffer byteBuffer,
                       final ServerIOReactor ioReactor) throws Exception {
        BufferBean bufferBean;
        bufferBean = ioReactor.getResponseBuffer().findItem(header.getSequenceNo());
        if (bufferBean != null) {
            DeliverSMMessage waitingDeliverSM = null;
            try {
                waitingDeliverSM = (DeliverSMMessage) bufferBean.getWaitingObject();
            } catch (final Exception e) {
                ioReactor.getLogger().error("DeliverResponseHandler", "handle", 0, null, " : Error : " + e.getMessage(), e);
                return;
            } finally {
                bufferBean.release();
            }
            if (waitingDeliverSM == null) {
                ioReactor.getLogger().error("DeliverResponseHandler", "handle", 0, null, " : Error : DeliverSM should not have been null!!");
                return;
            }
            if (ioReactor.getLogger().isDebugEnabled()) {
                ioReactor.getLogger().debug("DeliverResponseHandler",
                                            "handle",
                                            waitingDeliverSM.getTransactionId(),
                                            ioReactor.getLabel(),
                                            " Msg Ref : " + waitingDeliverSM.getMessageIdentifier());
            }
            if (ioReactor.getConnectionInformation().getConnectionState().isSuspended()) {
                final int freeItemsCount = ioReactor.getResponseBuffer().getFreeItemCount();
                if (freeItemsCount > (ioReactor.getResponseBuffer().getBufferSize() / 2)) {
                    if (ioReactor.getLogger().isInfoEnabled()) {
                        ioReactor.getLogger().info("DeliverResponseHandler",
                                                   "handle",
                                                   0,
                                                   ioReactor.getLabel(),
                                                   " : buffer free . Starting " + ioReactor.getLabel() + ". Sanity green.");
                    }
                    ioReactor.getConnectionInformation().getConnectionState().idle();
                }
            }
            final int errorNo = header.getCommandStatus();
            if (errorNo == Smpp34ErrorCodes.ERROR_CODE_ROK) {
                ioReactor.getStatisticCollector().increaseTotalReceivedSuccessfullDeliverSMCount();
                final IMessageProcessor messageProcessor = waitingDeliverSM.getMessageProcessor();
                boolean allResponseReceived = true;
                if (messageProcessor != null) {
                    allResponseReceived = messageProcessor.responseReceived(header.getSequenceNo(), waitingDeliverSM.getMessageIdentifier());
                }
                if (allResponseReceived) {
                    waitingDeliverSM.setSendResult(ESendResult.SUCCESS);
                    if (waitingDeliverSM.getWaitObject() == null) {
                        ioReactor.getServerCallback().deliveryResult(ioReactor.getConnectionInformation(),
                                                                     waitingDeliverSM,
                                                                     waitingDeliverSM.getAttachedObject());
                    } else {
                        synchronized (waitingDeliverSM.getWaitObject()) {
                            waitingDeliverSM.getWaitObject().notify();
                        }
                    }
                }
                return;
            } else if ((errorNo == Smpp34ErrorCodes.ERROR_CODE_RSYSERR)
                       || (errorNo == Smpp34ErrorCodes.ERROR_CODE_RMSGQFUL)
                       || (errorNo == Smpp34ErrorCodes.ERROR_CODE_RTHROTTLED)) {
                ioReactor.getStatisticCollector().increaseTotalReceivedRetryDeliverSMCount();
                waitingDeliverSM.setSendResult(ESendResult.RETRY);
                final IMessageProcessor messageProcessor = waitingDeliverSM.getMessageProcessor();
                messageProcessor.errorReceived();
                if (waitingDeliverSM.getWaitObject() == null) {
                    ioReactor.getServerCallback().deliveryResult(ioReactor.getConnectionInformation(), waitingDeliverSM, waitingDeliverSM.getAttachedObject());
                } else {
                    waitingDeliverSM.getWaitObject().notify();
                }

                return;
            } else if ((errorNo > Smpp34ErrorCodes.ERROR_CODE_ROK)
                       && (errorNo != Smpp34ErrorCodes.ERROR_CODE_RINVDSTADR)
                       && (errorNo != Smpp34ErrorCodes.ERROR_CODE_RCANCELFAIL)
                       && (errorNo < Smpp34ErrorCodes.ERROR_CODE_RMSGQFUL)) {
                ioReactor.getStatisticCollector().increaseTotalReceivedFailedDliverSMCount();
                waitingDeliverSM.setSendResult(ESendResult.FATAL_ERROR);
                final IMessageProcessor messageProcessor = waitingDeliverSM.getMessageProcessor();
                messageProcessor.errorReceived();
                if (waitingDeliverSM.getWaitObject() == null) {
                    ioReactor.getServerCallback().deliveryResult(ioReactor.getConnectionInformation(), waitingDeliverSM, waitingDeliverSM.getAttachedObject());
                } else {
                    waitingDeliverSM.getWaitObject().notify();
                }
                throw new SmppApiException(SmppApiException.FATAL_ERROR, SmppApiException.DOMAIN_SMPP_SERVER, "From Client we received error code : "
                                                                                                              + (new Integer(errorNo)).toString());
            } else if ((errorNo == 11) || (errorNo >= 20)) {
                ioReactor.getStatisticCollector().increaseTotalReceivedFailedDliverSMCount();
                waitingDeliverSM.setSendResult(ESendResult.ERROR);
                final IMessageProcessor messageProcessor = waitingDeliverSM.getMessageProcessor();
                messageProcessor.errorReceived();
                if (waitingDeliverSM.getWaitObject() == null) {
                    ioReactor.getServerCallback().deliveryResult(ioReactor.getConnectionInformation(), waitingDeliverSM, waitingDeliverSM.getAttachedObject());
                } else {
                    waitingDeliverSM.getWaitObject().notify();
                }
                return;
            }
        } else {
            ioReactor.getLogger().error("DeliverResponseHandler", "handle", 0, ioReactor.getLabel(), "  Sequence is absent : " + header.getSequenceNo());
        }

    }
}
