package com.adenon.smpp.server.core;

import com.adenon.api.smpp.message.DeliverSMMessage;
import com.adenon.api.smpp.message.SubmitSMMessage;
import com.adenon.api.smpp.sdk.ConnectionInformation;
import com.adenon.smpp.server.callback.response.BindResponse;
import com.adenon.smpp.server.callback.response.SubmitResponse;
import com.adenon.smpp.server.message.ServerBindRequest;


public interface IServerCallback {

    public void disconnected(ConnectionInformation connectionInformation);

    public void deliveryResult(ConnectionInformation connectionInformation,
                               DeliverSMMessage deliverSM,
                               Object attachedObject);

    public BindResponse bindReceived(ServerBindRequest bindRequestMessage);

    public SubmitResponse submitSMReceived(ConnectionInformation connectionInformation,
                                           SubmitSMMessage submitSMMessage);
}
