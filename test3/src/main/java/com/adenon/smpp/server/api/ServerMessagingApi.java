package com.adenon.smpp.server.api;

import com.adenon.api.smpp.sdk.AdditionalParamatersDescriptor;
import com.adenon.api.smpp.sdk.AddressDescriptor;
import com.adenon.api.smpp.sdk.EDeliveryStatus;
import com.adenon.api.smpp.sdk.IMessage;
import com.adenon.smpp.server.core.IDeliveryResult;
import com.adenon.smpp.server.core.ServerApiDelegator;


public class ServerMessagingApi {

    private final ServerApiDelegator serverApiDelegator;

    public ServerMessagingApi(final ServerApiDelegator serverApiDelegator) {
        this.serverApiDelegator = serverApiDelegator;
    }

    public IDeliveryResult sendDelivery(final String connectionName,
                                        final String messageIdentifier,
                                        final EDeliveryStatus deliveryStatus,
                                        final long transactionId,
                                        final AddressDescriptor destinationAddress,
                                        final AddressDescriptor originatingAddress,
                                        final AdditionalParamatersDescriptor paramatersDescriptor,
                                        final Object attachedObject) {
        return this.serverApiDelegator.getServerMessagingManager().sendDelivery(connectionName,
                                                                                messageIdentifier,
                                                                                deliveryStatus,
                                                                                transactionId,
                                                                                destinationAddress,
                                                                                originatingAddress,
                                                                                paramatersDescriptor,
                                                                                attachedObject);

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
        return this.serverApiDelegator.getServerMessagingManager().syncSendDelivery(connectionName,
                                                                                    messageIdentifier,
                                                                                    deliveryStatus,
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
        return this.serverApiDelegator.getServerMessagingManager().sendDeliverSM(connectionName,
                                                                                 messageDescriptor,
                                                                                 transactionId,
                                                                                 destinationAddress,
                                                                                 originatingAddress,
                                                                                 paramatersDescriptor,
                                                                                 attachedObject);

    }

    public IDeliveryResult syncSendDeliverSM(final String connectionName,
                                             final IMessage messageDescriptor,
                                             final long transactionId,
                                             final AddressDescriptor destinationAddress,
                                             final AddressDescriptor originatingAddress,
                                             final AdditionalParamatersDescriptor paramatersDescriptor,
                                             final Object attachedObject,
                                             final long waitTimeout) {
        return this.serverApiDelegator.getServerMessagingManager().syncSendDeliverSM(connectionName,
                                                                                     messageDescriptor,
                                                                                     transactionId,
                                                                                     destinationAddress,
                                                                                     originatingAddress,
                                                                                     paramatersDescriptor,
                                                                                     attachedObject,
                                                                                     waitTimeout);

    }
}
