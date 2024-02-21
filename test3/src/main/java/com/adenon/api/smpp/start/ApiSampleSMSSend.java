package com.adenon.api.smpp.start;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Level;

import com.adenon.api.smpp.SmppApi;
import com.adenon.api.smpp.SmppApiEngine;
import com.adenon.api.smpp.SmppConnectionApi;
import com.adenon.api.smpp.SmppMessagingApi;
import com.adenon.api.smpp.common.State;
import com.adenon.api.smpp.message.DeliverSMMessage;
import com.adenon.api.smpp.message.Smpp34QuerySMResponse;
import com.adenon.api.smpp.message.SubmitMultiSMResponse;
import com.adenon.api.smpp.message.SubmitSMMessage;
import com.adenon.api.smpp.sample.ApiSampleSMSSendThread;
import com.adenon.api.smpp.sample.BlockingTpsCounter;
import com.adenon.api.smpp.sdk.AlarmCode;
import com.adenon.api.smpp.sdk.ApiProperties;
import com.adenon.api.smpp.sdk.ConnectionDescriptor;
import com.adenon.api.smpp.sdk.ConnectionGroupDescriptor;
import com.adenon.api.smpp.sdk.ConnectionInformation;
import com.adenon.api.smpp.sdk.EventCode;
import com.adenon.api.smpp.sdk.ISmppCallback;
import com.adenon.api.smpp.sdk.LogDescriptor;
import com.adenon.api.smpp.sdk.LogType;
import com.adenon.api.smpp.sdk.SmppConnectionType;

public class ApiSampleSMSSend implements ISmppCallback {

    private final SmppMessagingApi smppMessagingApi;
    private final AtomicInteger    deliveryCounter = new AtomicInteger(0);
    private final AtomicInteger    ackCounter      = new AtomicInteger(0);
    private final AtomicInteger    sendCounter     = new AtomicInteger(0);

    public ApiSampleSMSSend(final SmppMessagingApi smppMessagingApi) {
        this.smppMessagingApi = smppMessagingApi;
    }

    @Override
    public void alarm(final ConnectionInformation hostInformation,
                      final AlarmCode alarmCode,
                      final String alarmDescription) {
    }

    @Override
    public void binded(final ConnectionInformation hostInformation) {
        // for (int i = 0; i < 5; i++) {
        // final ApiSampleSMSSendThread sendThread = new
        // ApiSampleSMSSendThread(this.smppMessagingApi, 2);
        // sendThread.start();
        // }
    }

    @Override
    public boolean cancelResult(final ConnectionInformation hostInformation,
                                final int _sequence,
                                final int _errorCode,
                                final String msg_id) {
        return false;
    }

    @Override
    public void connected(final ConnectionInformation hostInformation) {
    }

    @Override
    public boolean deliverSMReceived(final ConnectionInformation hostInformation,
                                     final DeliverSMMessage deliverSM) {
        System.err.println("I got *DELIVER_SM* : " + deliverSM.getParamShortMessage());
        return true;
    }

    @Override
    public boolean deliveryReceived(final ConnectionInformation hostInformation,
                                    final DeliverSMMessage deliverSM,
                                    final int messageStatus) {
        final int incrementAndGet = this.deliveryCounter.incrementAndGet();
        return true;
    }

    @Override
    public void disconnected(final ConnectionInformation hostInformation) {
    }

    @Override
    public boolean submitResult(final ConnectionInformation hostInformation,
                                final SubmitSMMessage submitSM,
                                final Object returnObject) {
        final int incrementAndGet = this.ackCounter.incrementAndGet();
        return true;
    }

    @Override
    public boolean submitSMReceived(final ConnectionInformation hostInformation,
                                    final SubmitSMMessage submitSM) {
        return true;
    }

    public static void main(final String[] args) {
        if (args.length != 3) {
            System.out.println("[thread count] [Message Count] [TPS Count]");
            return;
        }
        int senderThreadCount = Integer.parseInt(args[0]);
        int threadMessageCount = Integer.parseInt(args[1]);
        int tpsCount = Integer.parseInt(args[2]);

        final SmppApiEngine apiEngine = SmppApiEngine.getSmppApiEngine("myEngine", 500);
        final SmppApi smppApi = apiEngine.getSmppApi(LogDescriptor.getDefaultLogDescriptor()
                                                                  .setWriteConsole(true)
                                                                  .setLevel(Level.ERROR)
                                                                  .setLogType(LogType.LogConnectionsSeparetly),
                                                     new ApiProperties().setThreadCount(100));
        final SmppConnectionApi smppConnectionApi = smppApi.getSmppConnectionApi();
        final SmppMessagingApi smppMessagingApi = smppApi.getSmppMessagingApi();
        final ApiSampleSMSSend apiSampleSMSSend = new ApiSampleSMSSend(smppMessagingApi);
        final ConnectionGroupDescriptor connectionGroup = smppConnectionApi.generateConnectionGroup("Adenon");
        final ConnectionDescriptor connectionDescriptor = connectionGroup.generateConnection("con1")
                                                                         .addIp("127.0.0.1")
                                                                         .setPort(5101)
                                                                         .setUsername("a")
                                                                         .setPassword("a")
                                                                         .setTraceON(true)
                                                                         .setMaxThreadCount(50)
                                                                         .setCallbackInterface(apiSampleSMSSend)
                                                                         .setConnectionType(SmppConnectionType.BOTH)
                                                                         .setTps(100);

        connectionGroup.addConnection(connectionDescriptor);

        try {
            smppConnectionApi.createConnectionGroup(connectionGroup);
        } catch (final Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        BlockingTpsCounter tpsCounter = new BlockingTpsCounter(tpsCount);
        final State state = smppConnectionApi.getState();
        if (state.waitIdle()) {
            for (int i = 0; i < senderThreadCount; i++) {
                ApiSampleSMSSendThread apiSampleSMSSendThread = new ApiSampleSMSSendThread(smppMessagingApi, threadMessageCount, tpsCounter, state);
                apiSampleSMSSendThread.start();
            }

        }

        try {
            Thread.sleep(1000000000L);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unbinded(final ConnectionInformation hostInformation) {

    }

    @Override
    public boolean submitMultiResult(final ConnectionInformation hostInformation,
                                     final SubmitMultiSMResponse submitMultiSMResponse,
                                     final Object returnObject) {
        return false;
    }

    @Override
    public boolean queryResult(final ConnectionInformation hostInformation,
                               final Smpp34QuerySMResponse smpp34QuerySMResponse) {
        return false;
    }

    @Override
    public void event(final ConnectionInformation hostInformation,
                      final EventCode alarmCode,
                      final Object eventObject) {

    }

    public AtomicInteger getSendCounter() {
        return this.sendCounter;
    }

}
