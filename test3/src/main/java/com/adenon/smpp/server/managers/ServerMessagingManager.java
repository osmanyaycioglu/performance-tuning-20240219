package com.adenon.smpp.server.managers;

import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.logging.LoggerWrapper;
import com.adenon.api.smpp.message.DeliverSMMessage;
import com.adenon.api.smpp.sdk.AdditionalParamatersDescriptor;
import com.adenon.api.smpp.sdk.AddressDescriptor;
import com.adenon.api.smpp.sdk.EDeliveryStatus;
import com.adenon.api.smpp.sdk.IMessage;
import com.adenon.api.smpp.sdk.SmppConnectionType;
import com.adenon.smpp.server.core.DeliveryResult;
import com.adenon.smpp.server.core.EDeliveryResult;
import com.adenon.smpp.server.core.IDeliveryResult;
import com.adenon.smpp.server.core.ServerApiDelegator;
import com.adenon.smpp.server.core.ServerConnectionStore;
import com.adenon.smpp.server.core.ServerIOReactor;


public class ServerMessagingManager {


    private final ServerApiDelegator serverApiDelegator;
    private final LoggerWrapper      logger;


    public ServerMessagingManager(final ServerApiDelegator serverApiDelegator) {
        this.serverApiDelegator = serverApiDelegator;
        this.logger = this.serverApiDelegator.getLogManager().getLogger();


    }

    public IDeliveryResult sendDelivery(final String connectionName,
                                        final String messageIdentifier,
                                        final EDeliveryStatus deliveryStatus,
                                        final long transactionId,
                                        final AddressDescriptor destinationAddress,
                                        final AddressDescriptor originatingAddress,
                                        final AdditionalParamatersDescriptor paramatersDescriptor,
                                        final Object attachedObject) {

        return this.sendDeliveryMessage(connectionName,
                                        messageIdentifier,
                                        true,
                                        deliveryStatus,
                                        null,
                                        transactionId,
                                        destinationAddress,
                                        originatingAddress,
                                        paramatersDescriptor,
                                        attachedObject,
                                        -1);
    }


    public IDeliveryResult syncSendDelivery(final String connectionName,
                                            final String messageIdentifier,
                                            final EDeliveryStatus deliveryStatus,
                                            final long transactionId,
                                            final AddressDescriptor destinationAddress,
                                            final AddressDescriptor originatingAddress,
                                            final AdditionalParamatersDescriptor paramatersDescriptor,
                                            final Object attachedObject,
                                            final long waitTimeout) {
        return this.sendDeliveryMessage(connectionName,
                                        messageIdentifier,
                                        true,
                                        deliveryStatus,
                                        null,
                                        transactionId,
                                        destinationAddress,
                                        originatingAddress,
                                        paramatersDescriptor,
                                        attachedObject,
                                        waitTimeout);
    }


    public IDeliveryResult sendDeliverSM(final String connectionName,
                                         final IMessage messageDescriptor,
                                         final long transactionId,
                                         final AddressDescriptor destinationAddress,
                                         final AddressDescriptor originatingAddress,
                                         final AdditionalParamatersDescriptor paramatersDescriptor,
                                         final Object attachedObject) {

        return this.sendDeliveryMessage(connectionName,
                                        null,
                                        false,
                                        null,
                                        messageDescriptor,
                                        transactionId,
                                        destinationAddress,
                                        originatingAddress,
                                        paramatersDescriptor,
                                        attachedObject,
                                        -1);
    }


    public IDeliveryResult syncSendDeliverSM(final String connectionName,
                                             final IMessage messageDescriptor,
                                             final long transactionId,
                                             final AddressDescriptor destinationAddress,
                                             final AddressDescriptor originatingAddress,
                                             final AdditionalParamatersDescriptor paramatersDescriptor,
                                             final Object attachedObject,
                                             final long waitTimeout) {
        return this.sendDeliveryMessage(connectionName,
                                        null,
                                        false,
                                        null,
                                        messageDescriptor,
                                        transactionId,
                                        destinationAddress,
                                        originatingAddress,
                                        paramatersDescriptor,
                                        attachedObject,
                                        waitTimeout);
    }

    private IDeliveryResult sendDeliveryMessage(final String connectionName,
                                                final String messageIdentifier,
                                                final boolean isDelivery,
                                                final EDeliveryStatus deliveryStatus,
                                                final IMessage message,
                                                final long transactionId,
                                                final AddressDescriptor destinationAddress,
                                                final AddressDescriptor originatingAddress,
                                                final AdditionalParamatersDescriptor paramatersDescriptor,
                                                final Object returnObject,
                                                final long waitTimeout) {

        final ServerConnectionStore serverConnectionStore = this.serverApiDelegator.getServerConnectionStore();
        final ServerIOReactor ioReactor = serverConnectionStore.get(connectionName);
        if (ioReactor == null) {
            this.logger.error("ServerMessagingManager", "sendDeliverMessage", 0, null, " : No connection with given name : " + connectionName);
            final DeliveryResult deliveryResult = new DeliveryResult(EDeliveryResult.DeliveryFailed,
                                                                     IDeliveryResult.ERROR_CAUSE_NO_CONNECTION,
                                                                     "There is no connection with given name.",
                                                                     null,
                                                                     transactionId);
            return deliveryResult;
        }

        if ((ioReactor.getBindType() == SmppConnectionType.BOTH) || (ioReactor.getBindType() == SmppConnectionType.READ)) {
            DeliverSMMessage deliverSMMessage = null;
            if (transactionId <= 0) {
                deliverSMMessage = ioReactor.createDeliverSMMessage();
            } else {
                deliverSMMessage = ioReactor.createDeliverSMMessage(transactionId);
            }

            final long transID = deliverSMMessage.getTransactionId();
            long retVal = 0;
            try {
                deliverSMMessage.setMessage(message);
                deliverSMMessage.setDestinationAddress(destinationAddress);
                deliverSMMessage.setSourceAddress(originatingAddress);
                if (isDelivery) {
                    deliverSMMessage.setOpParamMessageId(messageIdentifier);
                    deliverSMMessage.setOpParamMessageState(deliveryStatus.getValue());
                    deliverSMMessage.setDelivery(true);
                    deliverSMMessage.setParamESMClass((deliverSMMessage.getParamESMClass() | 0x00000004));
                }
                if (paramatersDescriptor != null) {
                    deliverSMMessage.setOptParamsList(paramatersDescriptor.getOptionalParameters());
                    if (paramatersDescriptor.getValidityPeriod() > 0) {
                        deliverSMMessage.setParamValidityPeriod(CommonUtils.relativeTimeStringFromSeconds(paramatersDescriptor.getValidityPeriod()));
                    }
                    if (paramatersDescriptor.getMessageSchedulePeriod() > 0) {
                        deliverSMMessage.setParamScheduleDeliveryTime(CommonUtils.relativeTimeStringFromMinutes(paramatersDescriptor.getMessageSchedulePeriod()));
                    }
                }

                if (waitTimeout > 0) {
                    final Object waitObject = new Object();
                    deliverSMMessage.setWaitObject(waitObject);
                    try {
                        synchronized (waitObject) {
                            retVal = ioReactor.sendDeliverSM(deliverSMMessage, returnObject);
                            waitObject.wait(waitTimeout);
                        }
                    } catch (final Exception e) {
                        this.logger.error("ServerMessagingManager", "sendSms", transactionId, ioReactor.getLabel(), " : Error : " + e.getMessage(), e);
                        throw e;
                    }
                } else {
                    retVal = ioReactor.sendDeliverSM(deliverSMMessage, returnObject);
                }
            } catch (final Exception e) {
                this.logger.error("MessagingManager", "sendSms", transID, ioReactor.getLabel(), " : Error : " + e.getMessage(), e);
                return new DeliveryResult(EDeliveryResult.DeliveryFailed,
                                          IDeliveryResult.ERROR_CAUSE_NO_CONNECTION,
                                          "System error : " + e.getMessage(),
                                          null,
                                          transactionId);
            }
            if (retVal != 0) {
                return new DeliveryResult(EDeliveryResult.DeliveredSuccesfully, -1, null, deliverSMMessage, transactionId);
            } else {
                this.logger.error("ServerMessagingManager",
                                  "sendDeliveryMessage",
                                  0,
                                  null,
                                  "ServerMessagingManager->sendDeliveryMessage-> Error : Transaction Id is NULL! ");
                return new DeliveryResult(EDeliveryResult.DeliveryFailed,
                                          IDeliveryResult.ERROR_CAUSE_FATAL_ERROR,
                                          "Transaction Id is NULL",
                                          deliverSMMessage,
                                          transactionId);
            }
        } else {
            this.logger.error("ServerMessagingManager", "sendSms", 0, ioReactor.getLabel(), " Sending message is not allowed through this connection.");
            final DeliveryResult deliveryResult = new DeliveryResult(EDeliveryResult.DeliveryFailed,
                                                                     IDeliveryResult.ERROR_CAUSE_CONNECTION_READONLY,
                                                                     "Sending message is not allowed through this connection : " + ioReactor.getLabel(),
                                                                     null,
                                                                     transactionId);

            return deliveryResult;
        }
    }


}
