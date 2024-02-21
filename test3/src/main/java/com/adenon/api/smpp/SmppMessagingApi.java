package com.adenon.api.smpp;

import java.util.List;

import com.adenon.api.smpp.core.SmppApiDelegator;
import com.adenon.api.smpp.sdk.AdditionalParamatersDescriptor;
import com.adenon.api.smpp.sdk.AddressDescriptor;
import com.adenon.api.smpp.sdk.ConnectionInfo;
import com.adenon.api.smpp.sdk.IMessage;
import com.adenon.api.smpp.sdk.IRawMessages;
import com.adenon.api.smpp.sdk.ISMSSendResult;
import com.adenon.api.smpp.sdk.ISendResult;
import com.adenon.api.smpp.sdk.MessageInformation;
import com.adenon.api.smpp.sdk.SmppMessagingManager;


public class SmppMessagingApi {

    private final SmppApiDelegator smppApiDelegator;

    public SmppMessagingApi(final SmppApiDelegator smppApiDelegator) {
        this.smppApiDelegator = smppApiDelegator;
    }

    public ISMSSendResult sendMultiSms(final ConnectionInfo connectionDescriptor,
                                       final IMessage messageDescriptor,
                                       final int transactionId,
                                       final List<AddressDescriptor> destinationAddress,
                                       final AddressDescriptor originatingAddress,
                                       final AdditionalParamatersDescriptor paramatersDescriptor,
                                       final Object attachedObject) {
        this.smppApiDelegator.getBlockingTpsCounter().increase();
        return this.smppApiDelegator.getMessagingManager().sendMultiSms(connectionDescriptor,
                                                                        messageDescriptor,
                                                                        transactionId,
                                                                        destinationAddress,
                                                                        originatingAddress,
                                                                        paramatersDescriptor,
                                                                        attachedObject,
                                                                        -1);
    }

    public ISMSSendResult sendSms(final ConnectionInfo connectionDescriptor,
                                  final IMessage messageDescriptor,
                                  final long transactionId,
                                  final AddressDescriptor destinationAddress,
                                  final AddressDescriptor originatingAddress,
                                  final AdditionalParamatersDescriptor paramatersDescriptor,
                                  final Object attachedObject) {
        return this.smppApiDelegator.getMessagingManager().sendSms(connectionDescriptor,
                                                                   messageDescriptor,
                                                                   transactionId,
                                                                   destinationAddress,
                                                                   originatingAddress,
                                                                   paramatersDescriptor,
                                                                   attachedObject,
                                                                   -1,
                                                                   0);

    }

    public ISendResult sendRawSubmitSM(final ConnectionInfo connectionDescriptor,
                                       final IRawMessages rawMessage,
                                       final long transactionId,
                                       final Object attachedObject) {
        return this.smppApiDelegator.getMessagingManager().sendSubmitSMBytes(connectionDescriptor, rawMessage, transactionId, attachedObject, -1, 0);

    }

    public ISMSSendResult sendSms(final ConnectionInfo connectionDescriptor,
                                  final IMessage messageDescriptor,
                                  final long transactionId,
                                  final AddressDescriptor destinationAddress,
                                  final AddressDescriptor originatingAddress,
                                  final AdditionalParamatersDescriptor paramatersDescriptor,
                                  final Object attachedObject,
                                  final long blockIfNoConnectionPeriod) {
        return this.smppApiDelegator.getMessagingManager().sendSms(connectionDescriptor,
                                                                   messageDescriptor,
                                                                   transactionId,
                                                                   destinationAddress,
                                                                   originatingAddress,
                                                                   paramatersDescriptor,
                                                                   attachedObject,
                                                                   -1,
                                                                   blockIfNoConnectionPeriod);

    }

    public ISMSSendResult syncSendSms(final ConnectionInfo connectionDescriptor,
                                      final IMessage messageDescriptor,
                                      final long transactionId,
                                      final AddressDescriptor destinationAddress,
                                      final AddressDescriptor originatingAddress,
                                      final AdditionalParamatersDescriptor paramatersDescriptor,
                                      final Object attachedObject,
                                      final long waitTimeout) {
        return this.smppApiDelegator.getMessagingManager().syncSendSms(connectionDescriptor,
                                                                       messageDescriptor,
                                                                       transactionId,
                                                                       destinationAddress,
                                                                       originatingAddress,
                                                                       paramatersDescriptor,
                                                                       attachedObject,
                                                                       waitTimeout,
                                                                       0);

    }

    public ISMSSendResult syncSendSms(final ConnectionInfo connectionDescriptor,
                                      final IMessage messageDescriptor,
                                      final long transactionId,
                                      final AddressDescriptor destinationAddress,
                                      final AddressDescriptor originatingAddress,
                                      final AdditionalParamatersDescriptor paramatersDescriptor,
                                      final Object attachedObject,
                                      final long waitTimeout,
                                      final long blockIfNoConnectionPeriod) {
        return this.smppApiDelegator.getMessagingManager().syncSendSms(connectionDescriptor,
                                                                       messageDescriptor,
                                                                       transactionId,
                                                                       destinationAddress,
                                                                       originatingAddress,
                                                                       paramatersDescriptor,
                                                                       attachedObject,
                                                                       waitTimeout,
                                                                       blockIfNoConnectionPeriod);

    }

    public int sendQuerySm(final ConnectionInfo connectionInfo,
                           final String messageId,
                           final AddressDescriptor sourceAddress) throws Exception {
        return this.smppApiDelegator.getMessagingManager().sendQuerySm(connectionInfo, messageId, sourceAddress);
    }

    public int sendCancelSm(final ConnectionInfo connectionInfo,
                            final String messageId,
                            final AddressDescriptor sourceAddress,
                            final AddressDescriptor destinationAddress) throws Exception {
        return this.smppApiDelegator.getMessagingManager().sendCancelSm(connectionInfo, messageId, sourceAddress, destinationAddress);
    }

    public static MessageInformation getMessageInformation(final IMessage messageDescriptor) {
        return SmppMessagingManager.getMessageInformation(messageDescriptor);
    }

}
