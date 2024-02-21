package com.adenon.api.smpp.sdk;

import com.adenon.api.smpp.message.DeliverSMMessage;
import com.adenon.api.smpp.message.Smpp34QuerySMResponse;
import com.adenon.api.smpp.message.SubmitMultiSMResponse;
import com.adenon.api.smpp.message.SubmitSMMessage;

public interface ISmppCallback {

    public void connected(ConnectionInformation hostInformation);

    public void disconnected(ConnectionInformation hostInformation);

    public void binded(ConnectionInformation hostInformation);

    public void unbinded(ConnectionInformation hostInformation);

    public boolean deliverSMReceived(ConnectionInformation hostInformation,
                                     DeliverSMMessage deliverSM);

    public boolean submitSMReceived(ConnectionInformation hostInformation,
                                    SubmitSMMessage submitSM);

    public boolean submitMultiResult(ConnectionInformation hostInformation,
                                     SubmitMultiSMResponse submitMultiSMResponse,
                                     Object returnObject);

    public boolean deliveryReceived(ConnectionInformation hostInformation,
                                    DeliverSMMessage deliverSM,
                                    int messageStatus);

    public boolean submitResult(ConnectionInformation hostInformation,
                                SubmitSMMessage submitSM,
                                Object returnObject);

    public boolean cancelResult(ConnectionInformation hostInformation,
                                int sequenceNumber,
                                int errorCause,
                                String msgId);

    public boolean queryResult(ConnectionInformation hostInformation,
                               Smpp34QuerySMResponse smpp34QuerySMResponse);

    public void alarm(ConnectionInformation hostInformation,
                      AlarmCode alarmCode,
                      String alarmDescription);

    public void event(ConnectionInformation hostInformation,
                      EventCode alarmCode,
                      Object eventObject);

}
