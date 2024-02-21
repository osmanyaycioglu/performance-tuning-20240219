package com.adenon.api.smpp.sdk;

import com.adenon.api.smpp.message.DeliverSMMessage;
import com.adenon.api.smpp.message.Smpp34QuerySMResponse;
import com.adenon.api.smpp.message.SubmitMultiSMResponse;
import com.adenon.api.smpp.message.SubmitSMMessage;


public abstract class AbstractSmppCallback_V1 implements ISmppCallback {

    @Override
    public final void connected(final ConnectionInformation hostInformation) {
        this.connected(1, hostInformation);
    }

    @Override
    public final void disconnected(final ConnectionInformation hostInformation) {
        this.disconnected(1, hostInformation);
    }

    @Override
    public final void binded(final ConnectionInformation hostInformation) {
        this.binded(1, hostInformation);
    }

    @Override
    public final void unbinded(final ConnectionInformation hostInformation) {
        this.unbinded(1, hostInformation);
    }

    @Override
    public final boolean deliverSMReceived(final ConnectionInformation hostInformation,
                                           final DeliverSMMessage deliverSM) {

        return this.deliverSMReceived(1, hostInformation, deliverSM);
    }

    @Override
    public final boolean submitSMReceived(final ConnectionInformation hostInformation,
                                          final SubmitSMMessage submitSM) {
        return this.submitSMReceived(1, hostInformation, submitSM);
    }

    @Override
    public final boolean submitMultiResult(final ConnectionInformation hostInformation,
                                           final SubmitMultiSMResponse submitMultiSMResponse,
                                           final Object returnObject) {
        return this.submitMultiResult(1, hostInformation, submitMultiSMResponse, returnObject);
    }

    @Override
    public final boolean deliveryReceived(final ConnectionInformation hostInformation,
                                          final DeliverSMMessage deliverSM,
                                          final int messageStatus) {
        return this.deliveryReceived(1, hostInformation, deliverSM, messageStatus);
    }

    @Override
    public final boolean submitResult(final ConnectionInformation hostInformation,
                                      final SubmitSMMessage submitSM,
                                      final Object returnObject) {
        return this.submitResult(1, hostInformation, submitSM, returnObject);

    }

    @Override
    public final boolean cancelResult(final ConnectionInformation hostInformation,
                                      final int sequenceNumber,
                                      final int errorCause,
                                      final String msgId) {
        return this.cancelResult(1, hostInformation, sequenceNumber, errorCause, msgId);
    }

    @Override
    public final boolean queryResult(final ConnectionInformation hostInformation,
                                     final Smpp34QuerySMResponse smpp34QuerySMResponse) {
        return this.queryResult(1, hostInformation, smpp34QuerySMResponse);
    }

    @Override
    public final void alarm(final ConnectionInformation hostInformation,
                            final AlarmCode alarmCode,
                            final String alarmDescription) {
        this.alarm(1, hostInformation, alarmCode, alarmDescription);
    }

    @Override
    public final void event(final ConnectionInformation hostInformation,
                            final EventCode alarmCode,
                            final Object eventObject) {

    }


    abstract public void connected(int version,
                                   ConnectionInformation hostInformation);

    abstract public void disconnected(int version,
                                      ConnectionInformation hostInformation);

    abstract public void binded(int version,
                                ConnectionInformation hostInformation);

    abstract public void unbinded(int version,
                                  ConnectionInformation hostInformation);

    abstract public boolean deliverSMReceived(int version,
                                              ConnectionInformation hostInformation,
                                              DeliverSMMessage deliverSM);

    abstract public boolean submitSMReceived(int version,
                                             ConnectionInformation hostInformation,
                                             SubmitSMMessage submitSM);

    abstract public boolean submitMultiResult(int version,
                                              ConnectionInformation hostInformation,
                                              SubmitMultiSMResponse submitMultiSMResponse,
                                              Object returnObject);

    abstract public boolean deliveryReceived(int version,
                                             ConnectionInformation hostInformation,
                                             DeliverSMMessage deliverSM,
                                             int messageStatus);

    abstract public boolean submitResult(int version,
                                         ConnectionInformation hostInformation,
                                         SubmitSMMessage submitSM,
                                         Object returnObject);

    abstract public boolean cancelResult(int version,
                                         ConnectionInformation hostInformation,
                                         int sequenceNumber,
                                         int errorCause,
                                         String msgId);

    abstract public boolean queryResult(int version,
                                        ConnectionInformation hostInformation,
                                        Smpp34QuerySMResponse smpp34QuerySMResponse);

    abstract public void alarm(int version,
                               ConnectionInformation hostInformation,
                               AlarmCode alarmCode,
                               String alarmDescription);

}
