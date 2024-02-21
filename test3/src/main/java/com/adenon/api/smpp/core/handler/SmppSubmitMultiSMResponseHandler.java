package com.adenon.api.smpp.core.handler;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.common.Smpp34ErrorCodes;
import com.adenon.api.smpp.common.SmppApiException;
import com.adenon.api.smpp.core.SmppIOReactor;
import com.adenon.api.smpp.core.buffer.BufferBean;
import com.adenon.api.smpp.message.MessageHeader;
import com.adenon.api.smpp.message.SubmitMultiSMMessage;
import com.adenon.api.smpp.message.SubmitMultiSMResponse;
import com.adenon.api.smpp.messaging.processor.IMessageProcessor;
import com.adenon.api.smpp.sdk.ESendResult;

public class SmppSubmitMultiSMResponseHandler {

    public void handle(final MessageHeader header,
                       final SmppIOReactor smppIOReactor,
                       final ByteBuffer byteBuffer) throws Exception {
        // Parse the response message bytes.
        final SubmitMultiSMResponse submitMultiSMResponse = new SubmitMultiSMResponse();
        submitMultiSMResponse.parseMessage(byteBuffer);
        submitMultiSMResponse.setCommandStatus(header.getCommandStatus());

        // Find the request message corresponding to the response
        final BufferBean bufferBean = smppIOReactor.getResponseBuffer().findItem(header.getSequenceNo());

        if (bufferBean != null) {
            final SubmitMultiSMMessage waitingSubmitMultiSM = this.getCorrespondingRequestMessage(smppIOReactor, bufferBean);
            submitMultiSMResponse.setRequestMessage(waitingSubmitMultiSM);

            // Log if no request message found for the response.
            if (waitingSubmitMultiSM == null) {
                smppIOReactor.getLogger().error("SmppSubmitMultiSMResponseHandler", "handle", 0, null, " : Error : SubmitSM should not have been null!!");
                return;
            }

            // Log if request message found
            if (smppIOReactor.getLogger().isDebugEnabled()) {
                smppIOReactor.getLogger().debug("SmppSubmitSMResponseHandler",
                                                "handle",
                                                waitingSubmitMultiSM.getTransID(),
                                                smppIOReactor.getLabel(),
                                                " Msg Ref : " + submitMultiSMResponse.getMessageIdentifier());
            }

            if (smppIOReactor.getConnectionInformation().getConnectionState().isSuspended()) {
                final int freeItemsCount = smppIOReactor.getResponseBuffer().getFreeItemCount();
                if (freeItemsCount > (smppIOReactor.getResponseBuffer().getBufferSize() / 2)) {
                    if (smppIOReactor.getLogger().isInfoEnabled()) {
                        smppIOReactor.getLogger().info("SmppSubmitMultiSMResponseHandler",
                                                       "handle",
                                                       0,
                                                       smppIOReactor.getLabel(),
                                                       " : buffer free . Starting " + smppIOReactor.getLabel() + ". Sanity green.");
                    }
                    smppIOReactor.getConnectionInformation().getConnectionState().idle();
                }
            }

            final int errorNo = header.getCommandStatus();
            if (errorNo == Smpp34ErrorCodes.ERROR_CODE_ROK) { // Result OK
                // Increase the counter.
                smppIOReactor.getStatisticCollector().increaseTotalReceivedSuccessfullSubmitMultiSMCount();

                // boolean allResponseReceived = true;
                final IMessageProcessor messageProcessor = waitingSubmitMultiSM.getMessageProcessor();
                if (messageProcessor != null) {
                    messageProcessor.responseReceived(header.getSequenceNo(), submitMultiSMResponse.getMessageIdentifier());
                }

                // if (allResponseReceived) {

                // Send result is successful
                waitingSubmitMultiSM.setSendResult(ESendResult.SUCCESS);
                if (waitingSubmitMultiSM.getWaitObject() == null) { // for async request.
                    // Notify callback handler for async response.
                    smppIOReactor.getSmppCallback().submitMultiResult(smppIOReactor.getConnectionInformation(),
                                                                      submitMultiSMResponse,
                                                                      waitingSubmitMultiSM.getAttachedObject());
                } else { // for sync request
                    synchronized (waitingSubmitMultiSM.getWaitObject()) {
                        // Notify the thread waiting the response message.
                        waitingSubmitMultiSM.getWaitObject().notify();
                    }
                }

                // }
                return;
            } else if ((errorNo == Smpp34ErrorCodes.ERROR_CODE_RSYSERR)
                       || (errorNo == Smpp34ErrorCodes.ERROR_CODE_RMSGQFUL)
                       || (errorNo == Smpp34ErrorCodes.ERROR_CODE_RTHROTTLED)) { // Retriable error from SMSC
                // Increase the retry counter.
                smppIOReactor.getStatisticCollector().increaseTotalReceivedRetrySubmitMultiSMCount();

                // Send result is retry.
                waitingSubmitMultiSM.setSendResult(ESendResult.RETRY);
                final IMessageProcessor messageProcessor = waitingSubmitMultiSM.getMessageProcessor();
                messageProcessor.errorReceived();

                if (waitingSubmitMultiSM.getWaitObject() == null) { // async
                    smppIOReactor.getSmppCallback().submitMultiResult(smppIOReactor.getConnectionInformation(),
                                                                      submitMultiSMResponse,
                                                                      waitingSubmitMultiSM.getAttachedObject());
                } else { // sync
                    waitingSubmitMultiSM.getWaitObject().notify();
                }

                return;
            } else if ((errorNo > Smpp34ErrorCodes.ERROR_CODE_ROK)
                       && (errorNo != Smpp34ErrorCodes.ERROR_CODE_RINVDSTADR)
                       && (errorNo != Smpp34ErrorCodes.ERROR_CODE_RCANCELFAIL)
                       && (errorNo < Smpp34ErrorCodes.ERROR_CODE_RMSGQFUL)) { // These are
                                                                              // fatal errors
                                                                              // from SMSC.
                // increase the fail counter.
                smppIOReactor.getStatisticCollector().increaseTotalReceivedFailedSubmitMultiSMCount();

                // send result is fatal error.
                waitingSubmitMultiSM.setSendResult(ESendResult.FATAL_ERROR);
                final IMessageProcessor messageProcessor = waitingSubmitMultiSM.getMessageProcessor();
                messageProcessor.errorReceived();

                if (waitingSubmitMultiSM.getWaitObject() == null) { // async
                    smppIOReactor.getSmppCallback().submitMultiResult(smppIOReactor.getConnectionInformation(),
                                                                      submitMultiSMResponse,
                                                                      waitingSubmitMultiSM.getAttachedObject());
                } else { // sync
                    waitingSubmitMultiSM.getWaitObject().notify();
                }

                // Throw error for fatal errors received from SMSC
                throw new SmppApiException(SmppApiException.FATAL_ERROR, SmppApiException.DOMAIN_SMSC, "From SMSC we received error code : "
                                                                                                       + new Integer(errorNo).toString());
            } else if ((errorNo == 11 /*invalid dest address*/) || (errorNo >= 20 /*non-compansatable errors*/)) {
                // increase error counter.
                smppIOReactor.getStatisticCollector().increaseTotalReceivedFailedSubmitMultiSMCount();

                // send result is error.
                waitingSubmitMultiSM.setSendResult(ESendResult.ERROR);
                final IMessageProcessor messageProcessor = waitingSubmitMultiSM.getMessageProcessor();
                messageProcessor.errorReceived();

                if (waitingSubmitMultiSM.getWaitObject() == null) { // async
                    smppIOReactor.getSmppCallback().submitMultiResult(smppIOReactor.getConnectionInformation(),
                                                                      submitMultiSMResponse,
                                                                      waitingSubmitMultiSM.getAttachedObject());
                } else { // sync
                    waitingSubmitMultiSM.getWaitObject().notify();
                }
                return;
            }
        } else {
            smppIOReactor.getLogger().error("SmppSubmitSMResponseHandler",
                                            "handle",
                                            0,
                                            smppIOReactor.getLabel(),
                                            "  Sequence is absent : " + header.getSequenceNo());
        }

    }

    private SubmitMultiSMMessage getCorrespondingRequestMessage(final SmppIOReactor smppIOReactor,
                                                                final BufferBean bufferBean) throws Exception {
        SubmitMultiSMMessage waitingSubmitMultiSM = null;
        try {
            // Get waiting request message.
            waitingSubmitMultiSM = (SubmitMultiSMMessage) bufferBean.getWaitingObject();
        } catch (final Exception e) {
            smppIOReactor.getLogger().error("SmppSubmitSMResponseHandler", "handle", 0, null, " : Error : " + e.getMessage(), e);
            throw e;
        } finally {
            bufferBean.release();
        }
        return waitingSubmitMultiSM;
    }
}
