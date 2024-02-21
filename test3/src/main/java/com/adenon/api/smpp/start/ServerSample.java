package com.adenon.api.smpp.start;

import org.apache.log4j.Level;

import com.adenon.api.smpp.message.DeliverSMMessage;
import com.adenon.api.smpp.message.SubmitSMMessage;
import com.adenon.api.smpp.sdk.ConnectionInformation;
import com.adenon.api.smpp.sdk.LogDescriptor;
import com.adenon.smpp.server.api.ServerApi;
import com.adenon.smpp.server.api.ServerApiEngine;
import com.adenon.smpp.server.api.ServerConnectionApi;
import com.adenon.smpp.server.api.ServerMessagingApi;
import com.adenon.smpp.server.callback.response.BindResponse;
import com.adenon.smpp.server.callback.response.EBindResult;
import com.adenon.smpp.server.callback.response.ESubmitResult;
import com.adenon.smpp.server.callback.response.SubmitResponse;
import com.adenon.smpp.server.core.IServerCallback;
import com.adenon.smpp.server.core.ServerApiProperties;
import com.adenon.smpp.server.message.ServerBindRequest;


public class ServerSample implements IServerCallback {

    @Override
    public void disconnected(final ConnectionInformation connectionInformation) {

    }

    @Override
    public void deliveryResult(final ConnectionInformation connectionInformation,
                               final DeliverSMMessage deliverSM,
                               final Object attachedObject) {

    }

    @Override
    public BindResponse bindReceived(final ServerBindRequest bindRequestMessage) {
        System.out.println("BindReceived");
        final String username = bindRequestMessage.getUsername();
        final String password = bindRequestMessage.getPassword();

        BindResponse bindResponse = null;
        try {
            bindResponse = new BindResponse(EBindResult.BindSuccess, "sampleCon");

        } catch (final Exception e) {
            e.printStackTrace();
        }
        return bindResponse;
    }

    @Override
    public SubmitResponse submitSMReceived(final ConnectionInformation connectionInformation,
                                           final SubmitSMMessage submitSMMessage) {
        System.out.println("submitSMReceived");
        final SubmitResponse submitResponse = new SubmitResponse(ESubmitResult.submitSuccess, "msgid"
                                                                                              + System.currentTimeMillis()
                                                                                              + submitSMMessage.getSourceAddress().getNumber());
        return submitResponse;
    }

    public static void main(final String[] args) {
        final ServerSample serverSample = new ServerSample();
        final ServerApiEngine apiEngine = ServerApiEngine.getServerApiEngine("myServer");
        final ServerApi serverApi = apiEngine.getServerApi(serverSample,
                                                           LogDescriptor.getDefaultLogDescriptor().setWriteConsole(true).setLevel(Level.ERROR),
                                                           new ServerApiProperties().setTraceOn(true));
        final ServerConnectionApi serverConnectionApi = serverApi.getServerConnectionApi();

        final ServerMessagingApi serverMessagingApi = serverApi.getServerMessagingApi();
        try {
            Thread.sleep(1000000L);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

    }
}
