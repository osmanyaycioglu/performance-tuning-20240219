package com.adenon.api.smpp.sdk;

import java.util.List;

import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.common.SmppApiException;
import com.adenon.api.smpp.connection.SmppConnectionGroupManager;
import com.adenon.api.smpp.connection.SmppConnectionLocator;
import com.adenon.api.smpp.core.SmppIOReactor;
import com.adenon.api.smpp.logging.LogManager;
import com.adenon.api.smpp.logging.LoggerWrapper;
import com.adenon.api.smpp.message.SubmitMultiSMMessage;
import com.adenon.api.smpp.message.SubmitSMMessage;
import com.adenon.api.smpp.messaging.processor.IMessageProcessor;
import com.adenon.api.smpp.messaging.processor.RawMessageProcessor;
import com.adenon.api.smpp.messaging.processor.TextSmsProcessor;
import com.adenon.api.smpp.messaging.processor.WapPushProcessor;

public class SmppMessagingManager {

    private final LoggerWrapper         logger;

    private final LogManager            logManager;
    private final SmppConnectionLocator smppConnectionLocator;

    public SmppMessagingManager(final SmppConnectionGroupManager pConnectionManager,
                                final LogManager pLogManager,
                                final SmppConnectionLocator pSmppConnectionLocator) {
        this.smppConnectionLocator = pSmppConnectionLocator;
        this.logManager = pLogManager;
        this.logger = this.logManager.getLogControler().getLogger();

    }

    public ISMSSendResult syncSendSms(final ConnectionInfo connectionInfo,
                                      final IMessage message,
                                      final long transactionId,
                                      final AddressDescriptor destinationAddress,
                                      final AddressDescriptor originatingAddress,
                                      final AdditionalParamatersDescriptor paramatersDescriptor,
                                      final Object returnObject,
                                      final long waitTimeout,
                                      final long blockIfNoConnectionPeriod) {

        if (waitTimeout < 10) {
            return new SendResult(ISendResult.ERROR_CAUSE_INVALID, "Wait timeout should be more than 10 ms !!!");
        }
        return this.sendSms(connectionInfo,
                            message,
                            transactionId,
                            destinationAddress,
                            originatingAddress,
                            paramatersDescriptor,
                            returnObject,
                            waitTimeout,
                            blockIfNoConnectionPeriod);
    }

    public ISMSSendResult sendMultiSms(final ConnectionInfo connectionInfo,
                                       final IMessage message,
                                       final int transactionId,
                                       final List<AddressDescriptor> destinationAddress,
                                       final AddressDescriptor originatingAddress,
                                       final AdditionalParamatersDescriptor paramatersDescriptor,
                                       final Object returnObject,
                                       final long waitTimeout) {
        SmppIOReactor ioReactor = null;
        try {
            if (connectionInfo == null) {
                ioReactor = this.smppConnectionLocator.findAvaliableClientConnection(null, null, 1);
            } else {
                if (connectionInfo.checkConnectionInfoIsNull()) {
                    ioReactor = this.smppConnectionLocator.findAvaliableClientConnection(null, null, 1);
                } else {
                    ioReactor = this.smppConnectionLocator.findAvaliableClientConnection(connectionInfo.getConnectionGroupName(),
                                                                                         connectionInfo.getConnectionName(),
                                                                                         1);
                }
            }

        } catch (final SmppApiException e) {
            this.logger.error("MessagingManager", "sendSms", 0, null, " : Error : " + e.getMessage(), e);
            return new SendResult(ISendResult.ERROR_CAUSE_INTERNAL_ERROR, "Error occured while getting a valid connection. Desc : " + e.getMessage());
        } catch (final Exception e) {
            this.logger.error("MessagingManager", "sendSms", 0, null, " : Error : " + e.getMessage(), e);
            return new SendResult(ISendResult.ERROR_CAUSE_FATAL_ERROR, " Fatal Error : " + e.getMessage());
        }
        if (ioReactor == null) {
            if (connectionInfo == null) {
                this.logger.error("MessagingManager", "sendSms", 0, null, "Could not find free connected connection.");
            } else {
                this.logger.error("MessagingManager",
                                  "sendSms",
                                  0,
                                  null,
                                  "Could not find free connected connection group: "
                                          + connectionInfo.getConnectionGroupName()
                                          + " connection : "
                                          + connectionInfo.getConnectionName());
            }
            return new SendResult(ISendResult.ERROR_CAUSE_NO_CONNECTED_CONNECTION, "There is no valid connection");
        }
        return this.sendMultiSms(ioReactor, message, transactionId, destinationAddress, originatingAddress, paramatersDescriptor, returnObject, waitTimeout);
    }

    private ISMSSendResult sendMultiSms(final SmppIOReactor ioReactor,
                                        final IMessage message,
                                        final int transactionId,
                                        final List<AddressDescriptor> destinationAddress,
                                        final AddressDescriptor originatingAddress,
                                        final AdditionalParamatersDescriptor paramatersDescriptor,
                                        final Object returnObject,
                                        final long waitTimeout) {
        if (ioReactor.getConnectionDescriptor().getConnectionType() != SmppConnectionType.READ) {
            SubmitMultiSMMessage smpp34SubmitMultiSM = null;
            if (transactionId < 0) {
                smpp34SubmitMultiSM = ioReactor.createSubmitMultiSMMessage();
            } else {
                smpp34SubmitMultiSM = ioReactor.createSubmitMultiSMMessage(transactionId);
            }

            final long transID = smpp34SubmitMultiSM.getTransID();
            long retVal = 0;
            try {
                smpp34SubmitMultiSM.setMessage(message);
                smpp34SubmitMultiSM.setDestinationAddresses(destinationAddress);
                smpp34SubmitMultiSM.setSourceAddress(originatingAddress);
                boolean requestDelivery = false;
                final boolean putConcatHeader = false;
                if (paramatersDescriptor != null) {
                    if (paramatersDescriptor.isRequestDelivery()) {
                        requestDelivery = true;
                    }
                    // if (paramatersDescriptor.isPutConcatHeader()) {
                    // putConcatHeader = true;
                    // }
                    if (paramatersDescriptor.getValidityPeriod() > 0) {
                        smpp34SubmitMultiSM.setParamValidityPeriod(CommonUtils.relativeTimeStringFromSeconds(paramatersDescriptor.getValidityPeriod()));
                    }
                    if (paramatersDescriptor.getMessageSchedulePeriod() > 0) {
                        smpp34SubmitMultiSM.setParamScheduleDeliveryTime(CommonUtils.relativeTimeStringFromMinutes(paramatersDescriptor.getMessageSchedulePeriod()));
                    }
                }

                if (waitTimeout > 0) {
                    final Object waitObject = new Object();
                    smpp34SubmitMultiSM.setWaitObject(waitObject);
                    try {
                        synchronized (waitObject) {
                            retVal = ioReactor.sendSubmitSM(smpp34SubmitMultiSM, putConcatHeader, requestDelivery, returnObject);
                            waitObject.wait(waitTimeout);
                        }
                    } catch (final Exception e) {
                        this.logger.error("SmppMessagingManager", "sendSms", transactionId, ioReactor.getLabel(), " : Error : " + e.getMessage(), e);
                        throw e;
                    }
                } else {
                    retVal = ioReactor.sendSubmitSM(smpp34SubmitMultiSM, putConcatHeader, requestDelivery, returnObject);
                }
            } catch (final Exception e) {
                this.logger.error("MessagingManager", "sendSms", transID, ioReactor.getLabel(), " : Error : " + e.getMessage(), e);
                return new SendResult(ISendResult.ERROR_CAUSE_FATAL_ERROR, "System error : " + e.getMessage());
            }
            if (retVal != 0) {
                return new SendResult(ioReactor.getConnectionInformation(), transactionId, smpp34SubmitMultiSM);
            } else {
                return null;
            }
        } else {
            this.logger.error("MessagingManager", "sendSms", 0, ioReactor.getLabel(), " Sending message is not allowed through this connection.");
            return new SendResult(ISendResult.ERROR_CAUSE_CONNECTION_READONLY, "Sending message is not allowed through this connection : "
                                                                               + ioReactor.getLabel());
        }
    }

    public ISMSSendResult sendSms(final ConnectionInfo connectionInfo,
                                  final IMessage message,
                                  final long transactionId,
                                  final AddressDescriptor destinationAddress,
                                  final AddressDescriptor originatingAddress,
                                  final AdditionalParamatersDescriptor paramatersDescriptor,
                                  final Object returnObject,
                                  final long waitTimeout,
                                  final long blockIfNoConnectionPeriod) {
        SmppIOReactor ioReactor = null;
        int messageCount = 0;
        IMessageProcessor messageProcessor = null;
        try {
            switch (message.getMessageType()) {
                case SMSText:
                    final TextMessageDescriptor textMessageDescriptor = (TextMessageDescriptor) message;
                    final TextSmsProcessor textSmsProcessor = new TextSmsProcessor(textMessageDescriptor.getMessage(),
                                                                                   textMessageDescriptor.getDataCoding(),
                                                                                   this.logger,
                                                                                   transactionId,
                                                                                   "TextProcessing");
                    messageCount = textSmsProcessor.getMessagePartCount();
                    messageProcessor = textSmsProcessor;
                    break;

                case WAPPushSI:
                case WAPPushSL:
                case WAPBookmark:
                    final WapPushProcessor wapPushProcessor = new WapPushProcessor(message);
                    messageCount = wapPushProcessor.getMessagePartCount();
                    messageProcessor = wapPushProcessor;
                    break;

                default:
                    break;
            }

        } catch (final Exception e) {
            this.logger.error("MessagingManager", "sendSms", 0, null, " : Error : " + e.getMessage(), e);
            return new SendResult(ISendResult.ERROR_CAUSE_FATAL_ERROR, " Fatal Error : " + e.getMessage());
        }
        if (messageCount == 0) {
            return new SendResult(ISendResult.ERROR_CAUSE_FATAL_ERROR, " Message Count is 0 ");
        }
        try {
            final long startTime = System.currentTimeMillis();
            do {
                if (connectionInfo == null) {
                    ioReactor = this.smppConnectionLocator.findAvaliableClientConnection(null, null, messageCount);
                } else {
                    if (connectionInfo.checkConnectionInfoIsNull()) {
                        ioReactor = this.smppConnectionLocator.findAvaliableClientConnection(null, null, messageCount);
                    } else {
                        ioReactor = this.smppConnectionLocator.findAvaliableClientConnection(connectionInfo.getConnectionGroupName(),
                                                                                             connectionInfo.getConnectionName(),
                                                                                             messageCount);
                    }
                }
                if (ioReactor == null) {
                    Thread.sleep(5);
                }
            } while ((System.currentTimeMillis() < (blockIfNoConnectionPeriod + startTime)) && (ioReactor == null));
        } catch (final SmppApiException e) {
            this.logger.error("MessagingManager", "sendSms", 0, null, " : Error : " + e.getMessage(), e);
            return new SendResult(ISendResult.ERROR_CAUSE_INTERNAL_ERROR, "Error occured while getting a valid connection. Desc : " + e.getMessage());
        } catch (final Exception e) {
            this.logger.error("MessagingManager", "sendSms", 0, null, " : Error : " + e.getMessage(), e);
            return new SendResult(ISendResult.ERROR_CAUSE_FATAL_ERROR, " Fatal Error : " + e.getMessage());
        }
        if (ioReactor == null) {
            if (connectionInfo == null) {
                this.logger.error("MessagingManager", "sendSms", 0, null, "Could not find free connected connection.");
            } else {
                this.logger.error("MessagingManager",
                                  "sendSms",
                                  0,
                                  null,
                                  "Could not find free connected connection group: "
                                          + connectionInfo.getConnectionGroupName()
                                          + " connection : "
                                          + connectionInfo.getConnectionName());
            }
            return new SendResult(ESendResult.RETRY, ISendResult.ERROR_CAUSE_NO_CONNECTED_CONNECTION, "There is no valid connection");
        }
        return this.sendSms(ioReactor,
                            message,
                            transactionId,
                            destinationAddress,
                            originatingAddress,
                            paramatersDescriptor,
                            returnObject,
                            waitTimeout,
                            messageProcessor);
    }


    private ISMSSendResult sendSms(final SmppIOReactor ioReactor,
                                   final IMessage message,
                                   final long transactionId,
                                   final AddressDescriptor destinationAddress,
                                   final AddressDescriptor originatingAddress,
                                   final AdditionalParamatersDescriptor paramatersDescriptor,
                                   final Object returnObject,
                                   final long waitTimeout,
                                   final IMessageProcessor messageProcessor) {
        if (ioReactor.getConnectionDescriptor().getConnectionType() != SmppConnectionType.READ) {
            SubmitSMMessage smpp34SubmitSM = null;
            if (transactionId < 0) {
                smpp34SubmitSM = ioReactor.createSubmitSMMessage();
            } else {
                smpp34SubmitSM = ioReactor.createSubmitSMMessage(transactionId);
            }
            smpp34SubmitSM.setMessageProcessor(messageProcessor);
            final long transID = smpp34SubmitSM.getTransID();
            long retVal = 0;
            try {
                smpp34SubmitSM.setMessage(message);
                smpp34SubmitSM.setDestinationAddress(destinationAddress);
                smpp34SubmitSM.setSourceAddress(originatingAddress);
                boolean requestDelivery = false;
                boolean putConcatHeader = false;
                if (paramatersDescriptor != null) {
                    if (paramatersDescriptor.isRequestDelivery()) {
                        requestDelivery = true;
                    }
                    if (paramatersDescriptor.isPutConcatHeader()) {
                        putConcatHeader = true;
                    }
                    if (paramatersDescriptor.getValidityPeriod() > 0) {
                        smpp34SubmitSM.setParamValidityPeriod(CommonUtils.relativeTimeStringFromSeconds(paramatersDescriptor.getValidityPeriod()));
                    }
                    if (paramatersDescriptor.getMessageSchedulePeriod() > 0) {
                        smpp34SubmitSM.setParamScheduleDeliveryTime(CommonUtils.relativeTimeStringFromMinutes(paramatersDescriptor.getMessageSchedulePeriod()));
                    }
                }

                if (waitTimeout > 0) {
                    final Object waitObject = new Object();
                    smpp34SubmitSM.setWaitObject(waitObject);
                    try {
                        synchronized (waitObject) {
                            retVal = ioReactor.sendSubmitSM(smpp34SubmitSM, putConcatHeader, requestDelivery, returnObject);
                            waitObject.wait(waitTimeout);
                        }
                    } catch (final SmppApiException exp) {
                        this.logger.error("SmppMessagingManager",
                                          "sendSms",
                                          transactionId,
                                          ioReactor.getLabel(),
                                          " : SMPP API Exception Error : " + exp.getMessage(),
                                          exp);
                        return new SendResult(exp.getErrorCode(), "SMPP API  Exception Error : " + exp.getMessage());
                    } catch (final Exception e) {
                        this.logger.error("SmppMessagingManager", "sendSms", transactionId, ioReactor.getLabel(), " : Error : " + e.getMessage(), e);
                        return new SendResult(ISendResult.ERROR_CAUSE_FATAL_ERROR, "System error : " + e.getMessage());
                    }
                } else {
                    retVal = ioReactor.sendSubmitSM(smpp34SubmitSM, putConcatHeader, requestDelivery, returnObject);
                }
            } catch (final Exception e) {
                this.logger.error("MessagingManager", "sendSms", transID, ioReactor.getLabel(), " : Error : " + e.getMessage(), e);
                return new SendResult(ISendResult.ERROR_CAUSE_FATAL_ERROR, "System error : " + e.getMessage());
            }
            if (retVal != 0) {
                final SendResult mySendResult = new SendResult(ioReactor.getConnectionInformation(), transactionId, smpp34SubmitSM);
                ESendResult submitSMsendResult = smpp34SubmitSM.getSendResult();
                if (submitSMsendResult == null) {
                    submitSMsendResult = ESendResult.SUCCESS;
                }
                mySendResult.setSendResult(submitSMsendResult);
                return mySendResult;
            } else {
                return new SendResult(ISendResult.ERROR_CAUSE_FATAL_ERROR, "System error : return value should be greater than 0");
            }
        } else {
            this.logger.error("MessagingManager", "sendSms", 0, ioReactor.getLabel(), " Sending message is not allowed through this connection.");
            return new SendResult(ISendResult.ERROR_CAUSE_CONNECTION_READONLY, "Sending message is not allowed through this connection : "
                                                                               + ioReactor.getLabel());
        }
    }

    public int sendQuerySm(final ConnectionInfo connectionInfo,
                           final String messageId,
                           final AddressDescriptor sourceAddress) throws Exception {
        SmppIOReactor ioReactor = null;
        if (connectionInfo == null) {
            ioReactor = this.smppConnectionLocator.findAvaliableClientConnection(null, null, 1);
        } else {
            if (connectionInfo.checkConnectionInfoIsNull()) {
                ioReactor = this.smppConnectionLocator.findAvaliableClientConnection(null, null, 1);
            } else {
                ioReactor = this.smppConnectionLocator.findAvaliableClientConnection(connectionInfo.getConnectionGroupName(),
                                                                                     connectionInfo.getConnectionName(),
                                                                                     1);
            }
        }
        if (ioReactor == null) {
            throw new SmppApiException(SmppApiException.NULL, "IO Reactor is empty ");
        }
        return ioReactor.sendQuerySm(messageId, sourceAddress);
    }

    public int sendCancelSm(final ConnectionInfo connectionInfo,
                            final String messageId,
                            final AddressDescriptor sourceAddress,
                            final AddressDescriptor destinationAddress) throws Exception {
        SmppIOReactor ioReactor = null;
        if (connectionInfo == null) {
            ioReactor = this.smppConnectionLocator.findAvaliableClientConnection(null, null, 1);
        } else {
            if (connectionInfo.checkConnectionInfoIsNull()) {
                ioReactor = this.smppConnectionLocator.findAvaliableClientConnection(null, null, 1);
            } else {
                ioReactor = this.smppConnectionLocator.findAvaliableClientConnection(connectionInfo.getConnectionGroupName(),
                                                                                     connectionInfo.getConnectionName(),
                                                                                     1);
            }
        }
        if (ioReactor == null) {
            throw new SmppApiException(SmppApiException.NULL, "IO Reactor is empty ");
        }
        return ioReactor.sendCancelSm(messageId, sourceAddress, destinationAddress);

    }

    public static MessageInformation getMessageInformation(final IMessage messageDescriptor) {
        if (messageDescriptor == null) {
            return null;
        }
        final EMessageType messageType = messageDescriptor.getMessageType();
        switch (messageType) {
            case WAPBookmark:
            case WAPPushSL:
            case WAPPushSI:
                final WapPushProcessor wapPushProcessor = new WapPushProcessor();
                return wapPushProcessor.getMessageInformation(messageDescriptor);
            case SMSText:
                final TextSmsProcessor textSmsProcessor = new TextSmsProcessor();
                return textSmsProcessor.getMessageInformation(messageDescriptor);
            default:
                return null;
        }
    }

    public ISMSSendResult sendSubmitSMBytes(final ConnectionInfo connectionInfo,
                                            final IRawMessages rawMessage,
                                            final long transactionId,
                                            final Object returnObject,
                                            final long waitTimeout,
                                            final long blockIfNoConnectionPeriod) {
        SmppIOReactor ioReactor = null;
        if (rawMessage == null) {
            return new SendResult(ISendResult.ERROR_CAUSE_FATAL_ERROR, " Message Count is NULL!");
        }
        if (rawMessage.getMessageList() == null) {
            return new SendResult(ISendResult.ERROR_CAUSE_FATAL_ERROR, " Message Count is 0 ");
        }
        final int messageCount = rawMessage.getMessageList().size();
        if (messageCount == 0) {
            return new SendResult(ISendResult.ERROR_CAUSE_FATAL_ERROR, " Message Count is 0 ");
        }
        try {
            final long startTime = System.currentTimeMillis();
            do {
                if (connectionInfo == null) {
                    ioReactor = this.smppConnectionLocator.findAvaliableClientConnection(null, null, messageCount);
                } else {
                    if (connectionInfo.checkConnectionInfoIsNull()) {
                        ioReactor = this.smppConnectionLocator.findAvaliableClientConnection(null, null, messageCount);
                    } else {
                        ioReactor = this.smppConnectionLocator.findAvaliableClientConnection(connectionInfo.getConnectionGroupName(),
                                                                                             connectionInfo.getConnectionName(),
                                                                                             messageCount);
                    }
                }
                if (ioReactor == null) {
                    Thread.sleep(5);
                }
            } while ((System.currentTimeMillis() < (blockIfNoConnectionPeriod + startTime)) && (ioReactor == null));
        } catch (final SmppApiException e) {
            this.logger.error("MessagingManager", "sendSms", 0, null, " : Error : " + e.getMessage(), e);
            return new SendResult(ISendResult.ERROR_CAUSE_INTERNAL_ERROR, "Error occured while getting a valid connection. Desc : " + e.getMessage());
        } catch (final Exception e) {
            this.logger.error("MessagingManager", "sendSms", 0, null, " : Error : " + e.getMessage(), e);
            return new SendResult(ISendResult.ERROR_CAUSE_FATAL_ERROR, " Fatal Error : " + e.getMessage());
        }
        if (ioReactor == null) {
            if (connectionInfo == null) {
                this.logger.error("MessagingManager", "sendSms", 0, null, "Could not find free connected connection.");
            } else {
                this.logger.error("MessagingManager",
                                  "sendSms",
                                  0,
                                  null,
                                  "Could not find free connected connection group: "
                                          + connectionInfo.getConnectionGroupName()
                                          + " connection : "
                                          + connectionInfo.getConnectionName());
            }
            return new SendResult(ESendResult.RETRY, ISendResult.ERROR_CAUSE_NO_CONNECTED_CONNECTION, "There is no valid connection");
        }
        return this.sendRawSubmitSM(ioReactor, rawMessage, transactionId, returnObject, waitTimeout);
    }

    private ISMSSendResult sendRawSubmitSM(final SmppIOReactor ioReactor,
                                           final IRawMessages rawMessages,
                                           final long transactionId,
                                           final Object returnObject,
                                           final long waitTimeout) {
        if (ioReactor.getConnectionDescriptor().getConnectionType() != SmppConnectionType.READ) {
            SubmitSMMessage smpp34SubmitSM = null;
            final RawMessageProcessor messageProcessor = new RawMessageProcessor(rawMessages);
            messageProcessor.createHandler(rawMessages.getMessageList().size());
            if (transactionId < 0) {
                smpp34SubmitSM = ioReactor.createSubmitSMMessage();
            } else {
                smpp34SubmitSM = ioReactor.createSubmitSMMessage(transactionId);
            }
            smpp34SubmitSM.setRawMessages(rawMessages);

            smpp34SubmitSM.setMessageProcessor(messageProcessor);

            final long transID = smpp34SubmitSM.getTransID();
            long retVal = 0;
            try {
                if (waitTimeout > 0) {
                    final Object waitObject = new Object();
                    smpp34SubmitSM.setWaitObject(waitObject);
                    try {
                        synchronized (waitObject) {
                            retVal = ioReactor.sendSubmitSMBytes(smpp34SubmitSM, returnObject);
                            waitObject.wait(waitTimeout);
                        }
                    } catch (final SmppApiException exp) {
                        this.logger.error("SmppMessagingManager",
                                          "sendSms",
                                          transactionId,
                                          ioReactor.getLabel(),
                                          " : SMPP API Exception Error : " + exp.getMessage(),
                                          exp);
                        return new SendResult(exp.getErrorCode(), "SMPP API  Exception Error : " + exp.getMessage());
                    } catch (final Exception e) {
                        this.logger.error("SmppMessagingManager", "sendSms", transactionId, ioReactor.getLabel(), " : Error : " + e.getMessage(), e);
                        return new SendResult(ISendResult.ERROR_CAUSE_FATAL_ERROR, "System error : " + e.getMessage());
                    }
                } else {
                    retVal = ioReactor.sendSubmitSMBytes(smpp34SubmitSM, returnObject);
                }
            } catch (final Exception e) {
                this.logger.error("MessagingManager", "sendSms", transID, ioReactor.getLabel(), " : Error : " + e.getMessage(), e);
                return new SendResult(ISendResult.ERROR_CAUSE_FATAL_ERROR, "System error : " + e.getMessage());
            }
            if (retVal == 0) {
                return new SendResult(ISendResult.ERROR_CAUSE_FATAL_ERROR, "System error : return value should be greater than 0");
            }
            final SendResult mySendResult = new SendResult(ioReactor.getConnectionInformation(), transactionId, smpp34SubmitSM);
            ESendResult submitSMsendResult = smpp34SubmitSM.getSendResult();
            if (submitSMsendResult == null) {
                submitSMsendResult = ESendResult.SUCCESS;
            }
            mySendResult.setSendResult(submitSMsendResult);
            return mySendResult;

        } else {
            this.logger.error("MessagingManager", "sendSms", 0, ioReactor.getLabel(), " Sending message is not allowed through this connection.");
            return new SendResult(ISendResult.ERROR_CAUSE_CONNECTION_READONLY, "Sending message is not allowed through this connection : "
                                                                               + ioReactor.getLabel());
        }
    }

}
