package com.adenon.api.smpp.core.handler;

import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.Date;

import com.adenon.api.smpp.common.Smpp34ErrorCodes;
import com.adenon.api.smpp.common.SmppApiException;
import com.adenon.api.smpp.common.State;
import com.adenon.api.smpp.core.SmppIOReactor;
import com.adenon.api.smpp.core.buffer.BufferBean;
import com.adenon.api.smpp.logging.LoggerWrapper;
import com.adenon.api.smpp.message.MessageHeader;
import com.adenon.api.smpp.message.SubmitSMMessage;
import com.adenon.api.smpp.message.SubmitSMResponseMessage;
import com.adenon.api.smpp.messaging.processor.IMessageProcessor;
import com.adenon.api.smpp.sdk.ESendResult;
import com.adenon.api.smpp.sdk.EventCode;
import com.adenon.api.smpp.sdk.objects.QueueFullObject;
import com.adenon.api.smpp.sdk.objects.ThrottleObject;

public class SmppSubmitSMResponseHandler {

    public void handle(final MessageHeader header,
                       final SmppIOReactor smppIOReactor,
                       final ByteBuffer byteBuffer) throws Exception {
        final SubmitSMResponseMessage submitSMResponse = new SubmitSMResponseMessage();
        BufferBean bufferBean;
        submitSMResponse.parseMessage(byteBuffer);
        submitSMResponse.setCommandStatus(header.getCommandStatus());
        bufferBean = smppIOReactor.getResponseBuffer().findItem(header.getSequenceNo());
        if (bufferBean != null) {
            SubmitSMMessage waitingSubmitSM = null;
            try {
                waitingSubmitSM = (SubmitSMMessage) bufferBean.getWaitingObject();
            } catch (final Exception e) {
                smppIOReactor.getLogger().error("SmppSubmitSMResponseHandler", "handle", 0, null, " : Error : " + e.getMessage(), e);
                return;
            } finally {
                bufferBean.release();
            }
            if (waitingSubmitSM == null) {
                smppIOReactor.getLogger().error("SmppSubmitSMResponseHandler", "handle", 0, null, " : Error : SubmitSM should not have been null!!");
                return;
            }
            if (smppIOReactor.getLogger().isDebugEnabled()) {
                smppIOReactor.getLogger().debug("SmppSubmitSMResponseHandler",
                                                "handle",
                                                waitingSubmitSM.getTransID(),
                                                smppIOReactor.getLabel(),
                                                " Msg Ref : " + submitSMResponse.getMessageIdentifier());
            }
            if (smppIOReactor.getConnectionInformation().getConnectionState().isSuspended()) {
                // the following if statement is added to prevent the connection state change back to idle in case of shutdown or user originated
                // suspend.
                if (!smppIOReactor.isUserSuspended() && !smppIOReactor.isShutdown()) {
                    final int freeItemsCount = smppIOReactor.getResponseBuffer().getFreeItemCount();
                    if (freeItemsCount > (smppIOReactor.getResponseBuffer().getBufferSize() / 2)) {
                        if (smppIOReactor.getLogger().isInfoEnabled()) {
                            smppIOReactor.getLogger().info("SmppSubmitSMResponseHandler",
                                                           "handle",
                                                           0,
                                                           smppIOReactor.getLabel(),
                                                           " : buffer free . Starting " + smppIOReactor.getLabel() + ". Sanity green.");
                        }
                        smppIOReactor.getConnectionInformation().getConnectionState().idle();
                    }
                }
            }
            final int errorNo = header.getCommandStatus();
            waitingSubmitSM.setCommandStatus(errorNo);
            if (errorNo == Smpp34ErrorCodes.ERROR_CODE_ROK) {
                smppIOReactor.getStatisticCollector().increaseTotalReceivedSuccessfullSubmitSMCount();
                final IMessageProcessor messageProcessor = waitingSubmitSM.getMessageProcessor();
                boolean allResponseReceived = true;
                if (messageProcessor != null) {
                    allResponseReceived = messageProcessor.responseReceived(header.getSequenceNo(), submitSMResponse.getMessageIdentifier());
                }
                if (allResponseReceived) {
                    waitingSubmitSM.setSendResult(ESendResult.SUCCESS);
                    if (waitingSubmitSM.getWaitObject() == null) {
                        smppIOReactor.getSmppCallback().submitResult(smppIOReactor.getConnectionInformation(),
                                                                     waitingSubmitSM,
                                                                     waitingSubmitSM.getAttachedObject());
                    } else {
                        synchronized (waitingSubmitSM.getWaitObject()) {
                            waitingSubmitSM.getWaitObject().notify();
                        }
                    }

                }
                return;
            } else if ((errorNo == Smpp34ErrorCodes.ERROR_CODE_RSYSERR)
                       || (errorNo == Smpp34ErrorCodes.ERROR_CODE_RMSGQFUL)
                       || (errorNo == Smpp34ErrorCodes.ERROR_CODE_RTHROTTLED)) {
                smppIOReactor.getStatisticCollector().increaseTotalReceivedRetrySubmitSMCount();
                waitingSubmitSM.setSendResult(ESendResult.RETRY);
                final IMessageProcessor messageProcessor = waitingSubmitSM.getMessageProcessor();
                messageProcessor.errorReceived();

                final LoggerWrapper logger = smppIOReactor.getLogger();
                final long suspendPeriod = smppIOReactor.getConnectionDescriptor().getSuspendPeriod();
                if (errorNo == Smpp34ErrorCodes.ERROR_CODE_RMSGQFUL) {
                    final QueueFullObject queueFullObject = new QueueFullObject();
                    queueFullObject.setSuspendPeriod(suspendPeriod);
                    smppIOReactor.getSmppCallback().event(smppIOReactor.getConnectionInformation(), EventCode.QueueFullReceieved, queueFullObject);
                    smppIOReactor.setSuspendStartTime(System.currentTimeMillis());
                    smppIOReactor.setSuspendEndTime(System.currentTimeMillis() + queueFullObject.getSuspendPeriod());
                    smppIOReactor.getConnectionInformation().getConnectionState().suspended();

                    logger.warn("SmppSubmitSMResponseHandler",
                                "handle",
                                0,
                                " : WARN : ",
                                MessageFormat.format("QUEUEFULL Received >> conn: {0} suspended for {1} milliseconds",
                                                     smppIOReactor.getConnectionName(),
                                                     queueFullObject.getSuspendPeriod()));
                } else if (errorNo == Smpp34ErrorCodes.ERROR_CODE_RTHROTTLED) {
                    final State connectionState = smppIOReactor.getConnectionInformation().getConnectionState();
                    if (connectionState.isIdle()) {
                        final long throttleStartTime = smppIOReactor.getThrottleStartTime();
                        final long timeElapsedAfterLastThrottle = System.currentTimeMillis() - throttleStartTime;

                        final State state = smppIOReactor.getConnectionInformation().getConnectionState();
                        if (!state.isIdle()) {
                            logger.warn("SmppSubmitSMResponseHandler",
                                        "handle",
                                        0,
                                        " : WARN : ",
                                        MessageFormat.format("THROTTLE message while connection is in \"{1}\" state. >> conn: {0}",
                                                             smppIOReactor.getConnectionName(),
                                                             state));
                        } else if ((throttleStartTime > 0) && (timeElapsedAfterLastThrottle < 1500)) {
                            logger.warn("SmppSubmitSMResponseHandler",
                                        "handle",
                                        0,
                                        " : WARN : ",
                                        MessageFormat.format("Another THROTTLE message within 1500ms >> conn: {0}", smppIOReactor.getConnectionName()));
                        } else {
                            final ThrottleObject throttleObject = new ThrottleObject();
                            throttleObject.setReduceTpsBy(smppIOReactor.getConnectionDescriptor().getReduceTpsBy());
                            throttleObject.setThrottlePeriod(smppIOReactor.getConnectionDescriptor().getThrottlePeriod());
                            smppIOReactor.setThrottleStartTime(System.currentTimeMillis());
                            smppIOReactor.setThrottleEndTime(System.currentTimeMillis() + throttleObject.getThrottlePeriod());
                            final int avgOfLast3Tps = smppIOReactor.getConnectionInformation().getNonBlockingTpsCounter().getAverageTps();
                            final int oldMaxTps = smppIOReactor.getConnectionInformation().getNonBlockingTpsCounter().getMaxTps();
                            final int newMaxTps = (avgOfLast3Tps * (100 - throttleObject.getReduceTpsBy())) / 100;

                            // The TPS adjustment changed as follows. The commented line was changing max-tps of connection information also.
                            // So when connection return from throttled state its TPS was not changing.
                            // smppIOReactor.adjustTps(newMaxTps);
                            if (smppIOReactor.getConnectionInformation().getNonBlockingTpsCounter() != null) {
                                smppIOReactor.getConnectionInformation().getNonBlockingTpsCounter().adjustTps(newMaxTps);
                            }

                            // suspend the throttled connection for a while.
                            if (suspendPeriod > 0) {
                                smppIOReactor.setSuspendStartTime(System.currentTimeMillis());
                                smppIOReactor.setSuspendEndTime(System.currentTimeMillis() + suspendPeriod);
                                smppIOReactor.getConnectionInformation().getConnectionState().suspended();
                            }

                            logger.warn("SmppSubmitSMResponseHandler",
                                        "handle",
                                        0,
                                        " : WARN : ",
                                        MessageFormat.format("Connection THROTTLED >> conn: {0} (suspended for {4}ms), oldMaxTps: {1}, newMaxTps: {2} (avgOfLast3Tps: {5}), throttleEndTime: {3}",
                                                             smppIOReactor.getConnectionName(),
                                                             oldMaxTps,
                                                             newMaxTps,
                                                             new Date(smppIOReactor.getThrottleEndTime()),
                                                             suspendPeriod,
                                                             avgOfLast3Tps));
                        }
                    }
                }

                if (waitingSubmitSM.getWaitObject() == null) {
                    smppIOReactor.getSmppCallback()
                                 .submitResult(smppIOReactor.getConnectionInformation(), waitingSubmitSM, waitingSubmitSM.getAttachedObject());
                } else {
                    synchronized (waitingSubmitSM.getWaitObject()) {
                        waitingSubmitSM.getWaitObject().notify();
                    }
                }
                return;
            } else if ((errorNo > Smpp34ErrorCodes.ERROR_CODE_ROK)
                       && (errorNo != Smpp34ErrorCodes.ERROR_CODE_RINVDSTADR)
                       && (errorNo != Smpp34ErrorCodes.ERROR_CODE_RCANCELFAIL)
                       && (errorNo < Smpp34ErrorCodes.ERROR_CODE_RMSGQFUL)) {
                smppIOReactor.getStatisticCollector().increaseTotalReceivedFailedSubmitSMCount();
                waitingSubmitSM.setSendResult(ESendResult.FATAL_ERROR);
                final IMessageProcessor messageProcessor = waitingSubmitSM.getMessageProcessor();
                messageProcessor.errorReceived();
                if (waitingSubmitSM.getWaitObject() == null) {
                    smppIOReactor.getSmppCallback()
                                 .submitResult(smppIOReactor.getConnectionInformation(), waitingSubmitSM, waitingSubmitSM.getAttachedObject());
                } else {
                    synchronized (waitingSubmitSM.getWaitObject()) {
                        waitingSubmitSM.getWaitObject().notify();
                    }
                }
                throw new SmppApiException(SmppApiException.FATAL_ERROR, SmppApiException.DOMAIN_SMSC, "From SMSC we received error code : "
                                                                                                       + (new Integer(errorNo)).toString());
            } else if ((errorNo == 11) || (errorNo >= 20)) {
                smppIOReactor.getStatisticCollector().increaseTotalReceivedFailedSubmitSMCount();
                waitingSubmitSM.setSendResult(ESendResult.ERROR);
                final IMessageProcessor messageProcessor = waitingSubmitSM.getMessageProcessor();
                messageProcessor.errorReceived();
                if (waitingSubmitSM.getWaitObject() == null) {
                    smppIOReactor.getSmppCallback()
                                 .submitResult(smppIOReactor.getConnectionInformation(), waitingSubmitSM, waitingSubmitSM.getAttachedObject());
                } else {
                    synchronized (waitingSubmitSM.getWaitObject()) {
                        waitingSubmitSM.getWaitObject().notify();
                    }
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
}
