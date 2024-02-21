package com.adenon.api.smpp.sdk;

import com.adenon.api.smpp.message.DeliverSMMessage;
import com.adenon.api.smpp.message.Smpp34QuerySMResponse;
import com.adenon.api.smpp.message.SubmitMultiSMResponse;
import com.adenon.api.smpp.message.SubmitSMMessage;


public abstract class AbstractSmppCallback_V2 implements ISmppCallback {

    @Override
    public void connected(final ConnectionInformation hostInformation) {
        this.connected(2, hostInformation);
    }

    @Override
    public void disconnected(final ConnectionInformation hostInformation) {
        this.disconnected(2, hostInformation);
    }

    @Override
    public void binded(final ConnectionInformation hostInformation) {
        this.binded(2, hostInformation);
    }

    @Override
    public void unbinded(final ConnectionInformation hostInformation) {
        this.unbinded(2, hostInformation);
    }

    @Override
    public boolean deliverSMReceived(final ConnectionInformation hostInformation,
                                     final DeliverSMMessage deliverSM) {

        return this.deliverSMReceived(2, hostInformation, deliverSM);
    }

    @Override
    public boolean submitSMReceived(final ConnectionInformation hostInformation,
                                    final SubmitSMMessage submitSM) {
        return this.submitSMReceived(2, hostInformation, submitSM);
    }

    @Override
    public boolean submitMultiResult(final ConnectionInformation hostInformation,
                                     final SubmitMultiSMResponse submitMultiSMResponse,
                                     final Object returnObject) {
        return this.submitMultiResult(2, hostInformation, submitMultiSMResponse, returnObject);
    }

    @Override
    public boolean deliveryReceived(final ConnectionInformation hostInformation,
                                    final DeliverSMMessage deliverSM,
                                    final int messageStatus) {
        return this.deliveryReceived(2, hostInformation, deliverSM, messageStatus);
    }

    @Override
    public boolean submitResult(final ConnectionInformation hostInformation,
                                final SubmitSMMessage submitSM,
                                final Object returnObject) {
        return this.submitResult(2, hostInformation, submitSM, returnObject);

    }

    @Override
    public boolean cancelResult(final ConnectionInformation hostInformation,
                                final int sequenceNumber,
                                final int errorCause,
                                final String msgId) {
        return this.cancelResult(2, hostInformation, sequenceNumber, errorCause, msgId);
    }

    @Override
    public boolean queryResult(final ConnectionInformation hostInformation,
                               final Smpp34QuerySMResponse smpp34QuerySMResponse) {
        return this.queryResult(2, hostInformation, smpp34QuerySMResponse);
    }

    @Override
    public void alarm(final ConnectionInformation hostInformation,
                      final AlarmCode alarmCode,
                      final String alarmDescription) {
        this.alarm(2, hostInformation, alarmCode, alarmDescription);
    }

    @Override
    public void event(final ConnectionInformation hostInformation,
                      final EventCode alarmCode,
                      final Object eventObject) {
        this.event(2, hostInformation, alarmCode, eventObject);
    }


    abstract public void connected(int version,
                                   ConnectionInformation hostInformation);

    abstract public void disconnected(int version,
                                      ConnectionInformation hostInformation);

    abstract void binded(int version,
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

    abstract public void event(int version,
                               ConnectionInformation hostInformation,
                               EventCode alarmCode,
                               Object eventObject);


}
