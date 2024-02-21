package com.adenon.smpp.server.core;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.adenon.api.smpp.buffer.SendBufferObject;
import com.adenon.api.smpp.buffer.SmppBufferManager;
import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.common.SequenceGenerator;
import com.adenon.api.smpp.common.Smpp34Constants;
import com.adenon.api.smpp.common.SmppApiException;
import com.adenon.api.smpp.common.TransactionManager;
import com.adenon.api.smpp.core.ConnectionController;
import com.adenon.api.smpp.core.IOReactor;
import com.adenon.api.smpp.core.ISmppMessageHandler;
import com.adenon.api.smpp.core.buffer.BufferBean;
import com.adenon.api.smpp.core.buffer.ResponseBufferImplementation;
import com.adenon.api.smpp.logging.LoggerWrapper;
import com.adenon.api.smpp.message.BindResponseMessage;
import com.adenon.api.smpp.message.DeliverSMMessage;
import com.adenon.api.smpp.message.DeliverSMResponseMessage;
import com.adenon.api.smpp.message.MessageHeader;
import com.adenon.api.smpp.message.SubmitSMResponseMessage;
import com.adenon.api.smpp.sdk.ConnectionInformation;
import com.adenon.api.smpp.sdk.ESendResult;
import com.adenon.api.smpp.sdk.SmppConnectionType;
import com.adenon.smpp.server.callback.response.ESubmitResult;
import com.adenon.smpp.server.callback.response.SubmitResponse;

public class ServerIOReactor extends IOReactor {

    public final static long          MAX_NUMBER     = 99999999999999L;
    public final static long          MIN_NUMBER     = 1;

    private final ISmppMessageHandler messageHandler = new ServerMessageHandler(this);

    private IServerCallback           serverCallback;


    private SocketChannel             socketChannel;

    private static long               ack_num        = 0;

    private ServerApiDelegator        smppApiDelegator;
    private SmppConnectionType        bindType       = SmppConnectionType.BOTH;
    private final String              serverName;
    private String                    externalConnectionName;

    public ServerIOReactor(final LoggerWrapper logger,
                           final String serverName,
                           final ServerApiDelegator smppApiDelegator,
                           final SocketChannel socketChannel,
                           final String ip,
                           final int port) {
        super(logger);
        this.serverName = serverName;
        this.setSmppApiDelegator(smppApiDelegator);
        this.setConnectionInformation(new ConnectionInformation(this, "connectionState", serverName));
        this.getConnectionInformation().setIp(ip);
        this.getConnectionInformation().setConnected(true);
        this.getConnectionInformation().setPort(port);
        this.setIoReactorLock(new Object());
        this.setShutdown(false);
        this.socketChannel = socketChannel;
        this.setLabel("[" + serverName + "@connectionState]");

    }


    public void initialize() throws SmppApiException {
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("ServerIOReactor",
                                   "initialize",
                                   0,
                                   this.getLabel(),
                                   "Server IO Reactor is initializing. --> " + this.getConnectionInformation().toString());
        }
        this.setResponseBuffer(new ResponseBufferImplementation(this.getSmppApiDelegator().getApiProperties().getWindowSize(),
                                                                2,
                                                                this.getLogger(),
                                                                10000,
                                                                this.getLabel()));

        this.setServerCallback(this.getSmppApiDelegator().getServerCallback());

        this.setConnectionController(new ConnectionController(this));
        this.getConnectionController().setDaemon(true);
        this.getConnectionController().start();
        this.getConnectionInformation().getConnectionState().suspended();

    }


    public void cleanupConnection(final String reason) {
        try {
            try {
                this.getSmppApiDelegator().getServerConnectionStore().remove(this.getExternalConnectionName());
            } catch (final Exception e) {
            }
            try {
                this.smppApiDelegator.getSmppIOReactorStorage().remove(this);
            } catch (final Exception e) {
            }

            try {
                if (this.getSocketChannel() != null) {
                    this.getSocketChannel().socket().close();
                }
            } catch (final Exception e) {
            }
            try {
                if (this.getSocketChannel() != null) {
                    this.getSocketChannel().close();
                }
            } catch (final Exception e) {
            }
        } catch (final Exception e) {
            this.getLogger().error("ServerIOReactor", "closeConnection", 0, null, " : Error : " + e.getMessage(), e);
        } finally {
            try {
                this.getConnectionInformation().setConnected(false);
                this.setSocketChannel(null);
                this.getConnectionInformation().getConnectionState().stopped();
                this.getResponseBuffer().resetBuffer();
                this.serverCallback.disconnected(this.getConnectionInformation());
            } catch (final Exception e) {
                this.getLogger().error("ServerIOReactor", "closeConnection", 0, null, " : Error : " + e.getMessage(), e);
            } finally {
                this.getBinded().set(false);
                this.setThreadCount(0);
            }
        }
    }

    @Override
    public void closeConnection(final String description) {
        synchronized (this.getIoReactorLock()) {
            if (!this.getConnectionInformation().getConnectionState().isStopped() || this.isShutdown() || this.getConnectionInformation().isConnected()) {
                if (this.getLogger().isInfoEnabled()) {
                    this.getLogger().info("ServerIOReactor", "closeConnection", 0, this.getLabel(), "#CLOSING CONNECTION# Description : " + description);
                }
                try {
                    this.nackToWaitingObjects();
                } catch (final Exception e) {
                }
                this.cleanupConnection(description);
            }
        }
    }

    public void nackToWaitingObjects() {
        for (int i = 0; i < this.getResponseBuffer().getBufferBeans().length; i++) {
            try {
                if ((this.getResponseBuffer().getBufferBeans()[i].getStatus().get() == BufferBean.OBJECT_STATUS_READABLE)
                    && (this.getResponseBuffer().getBufferBeans()[i].getSequenceNumber() > 0)) {
                    if (this.getResponseBuffer().getBufferBeans()[i].getWaitingObject() != null) {
                        if (this.getResponseBuffer().getBufferBeans()[i].getWaitingObject().getMesssageType() == Smpp34Constants.MSG_DELIVER_SM) {
                            final DeliverSMMessage deliverSM = (DeliverSMMessage) this.getResponseBuffer().getBufferBeans()[i].getWaitingObject();
                            deliverSM.setSendResult(ESendResult.RETRY);
                            if (deliverSM.getWaitObject() == null) {
                                this.serverCallback.deliveryResult(this.getConnectionInformation(), deliverSM, deliverSM.getAttachedObject());
                            } else {
                                synchronized (deliverSM.getWaitObject()) {
                                    deliverSM.getWaitObject().notify();
                                }
                            }
                        }
                    }
                }
            } catch (final Exception e) {
                this.getLogger().error("ServerIOReactor", "nackToWaitingObjects", 0, null, " : Error : " + e.getMessage(), e);
            }
            try {
                this.getResponseBuffer().getBufferBeans()[i].release();
            } catch (final Exception e) {
                this.getLogger().error("ServerIOReactor", "nackToWaitingObjects", 0, null, " : Error : " + e.getMessage(), e);
            }
        }
    }

    public long sendDeliverSM(final DeliverSMMessage deliverSM,
                              final Object returnObject) throws Exception {

        deliverSM.init(returnObject);
        final int messageCount = deliverSM.getMessageProcessor().getMessagePartCount();
        if (messageCount > 0) {
            for (int i = 0; i < messageCount; i++) {
                final int sequenceNumber = this.getSequenceNumber();
                if (!deliverSM.isDelivery()) {
                    final int referenceNumber = this.getNextRefNumByte();
                    deliverSM.setOpParamSarMsgRefNum(referenceNumber);
                    deliverSM.setOpParamSarSegmentSequenceNum(i + 1);
                    deliverSM.setOpParamSarTotalSegments(messageCount);
                }
                final SendBufferObject nextBufferObject = SmppBufferManager.getNextBufferObject();
                if (nextBufferObject == null) {
                    throw new SmppApiException(SmppApiException.FATAL_ERROR, "No send buffer");
                }
                try {
                    deliverSM.getMessageProcessor().addSequence(i, sequenceNumber);
                    deliverSM.fillBuffer(nextBufferObject.getByteBuffer(), sequenceNumber, i);
                    this.sendMsg(deliverSM, sequenceNumber, nextBufferObject.getByteBuffer());
                } catch (final Exception e) {
                    this.getLogger().error("ServerIOReactor", "sendSubmitSM", 0, null, " : Error : " + e.getMessage(), e);
                    throw new SmppApiException(e);
                } finally {
                    SmppBufferManager.releaseBufferObject(nextBufferObject);
                }
            }
        }
        return deliverSM.getTransactionId();
    }


    public void sendSubmitSMResponse(final int seqno,
                                     final ByteBuffer byteBuffer,
                                     final SubmitResponse submitResponse) throws Exception {
        ESubmitResult submitResult = submitResponse.getSubmitResult();
        int status = 0;
        if (submitResult != null) {
            status = submitResult.getValue();
        } else {
            status = submitResponse.getExtraResult();
        }
        byteBuffer.clear();
        CommonUtils.createHeader(byteBuffer, Smpp34Constants.MSG_SUBMIT_SM_RESP, seqno, status);
        if (status == 0) {
            final SubmitSMResponseMessage submitSMResponse = new SubmitSMResponseMessage();
            submitSMResponse.setMessageIdentifier(submitResponse.getMessageId());
            submitSMResponse.setOptionalParameters(submitResponse.getOptionalParameters());
            submitSMResponse.fillBody(byteBuffer);
        }
        CommonUtils.setLength(byteBuffer);
        this.writeBuffer(byteBuffer);
    }

    public void sendDeliverSMResponse(final int seqno,
                                      final ByteBuffer byteBuffer,
                                      final int status) throws Exception {
        final DeliverSMResponseMessage deliverSMResponse = new DeliverSMResponseMessage();
        byteBuffer.clear();
        CommonUtils.createHeader(byteBuffer, Smpp34Constants.MSG_DELIVER_SM_RESP, seqno, status);
        deliverSMResponse.fillBody(byteBuffer);
        CommonUtils.setLength(byteBuffer);
        this.writeBuffer(byteBuffer);
    }

    public void sendBindResponse(final MessageHeader smpp34Header,
                                 final ByteBuffer byteBuffer,
                                 final BindResponseMessage bindResponseMessage,
                                 final int status) throws Exception {
        byteBuffer.clear();
        int responseType = Smpp34Constants.MSG_BIND_TRANSCVR_RESP;
        switch (smpp34Header.getCommandID()) {
            case Smpp34Constants.MSG_BIND_TRANSCVR:
                responseType = Smpp34Constants.MSG_BIND_TRANSCVR_RESP;
                break;
            case Smpp34Constants.MSG_BIND_TRANSMITTER:
                responseType = Smpp34Constants.MSG_BIND_TRANSMITTER_RESP;
                break;
            case Smpp34Constants.MSG_BIND_RECEIVER:
                responseType = Smpp34Constants.MSG_BIND_RECEIVER_RESP;
                break;
            default:
                responseType = Smpp34Constants.MSG_BIND_TRANSCVR_RESP;
                break;

        }
        CommonUtils.createHeader(byteBuffer, responseType, smpp34Header.getSequenceNo(), status);
        bindResponseMessage.fillBody(byteBuffer);
        CommonUtils.setLength(byteBuffer);
        this.writeBuffer(byteBuffer);
    }

    @Override
    public int getSequenceNumber() {
        return SequenceGenerator.getNextSequenceNum();
    }

    @Override
    public short getNextRefNum() {
        return SequenceGenerator.getNextRefNum();
    }

    @Override
    public int getNextRefNumByte() {
        return SequenceGenerator.getNextRefNumByte();
    }

    @Override
    public void sendAlive(final int seq) throws Exception {
        ByteBuffer enqLink = ByteBuffer.allocateDirect(30);
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("ServerIOReactor", "sendAlive", 0, this.getLabel(), " Sending Enquire Link . Sequence : " + seq);
        }
        CommonUtils.createAckHeader(enqLink, Smpp34Constants.MSG_ENQUIRE_LINK, seq, 0);
        this.writeBuffer(enqLink);
        enqLink = null;
    }

    @Override
    public void sendAliveRes(final int seq) throws Exception {
        ByteBuffer enqLink = ByteBuffer.allocate(20);
        CommonUtils.createAckHeader(enqLink, Smpp34Constants.MSG_ENQUIRE_LINK_RESP, seq, 0);
        this.writeBuffer(enqLink);
        enqLink = null;
    }

    public synchronized static long getNextAckNum() {
        ServerIOReactor.ack_num++;
        if (ServerIOReactor.ack_num > ServerIOReactor.MAX_NUMBER) {
            ServerIOReactor.ack_num = ServerIOReactor.MIN_NUMBER;
        }
        return ServerIOReactor.ack_num;
    }

    public DeliverSMMessage createDeliverSMMessage() {
        final DeliverSMMessage deliverSMMessage = new DeliverSMMessage(this.getLogger(), TransactionManager.getNextTransactionID(), this.getLabel());
        return deliverSMMessage;
    }

    public DeliverSMMessage createDeliverSMMessage(final long transactionId) {
        final DeliverSMMessage deliverSMMessage = new DeliverSMMessage(this.getLogger(), transactionId, this.getLabel());
        return deliverSMMessage;
    }

    public void restartConnection() {
        this.closeConnection("Restart command received.");
    }

    public int getWindowSize() {
        return this.getResponseBuffer().getBufferSize();
    }

    public int getSmsType(final int datacoding) {
        return 0;
    }

    public int getUsedBufferCount() {
        return this.getResponseBuffer().getUsedItemCount();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        return builder.toString();
    }


    public IServerCallback getServerCallback() {
        return this.serverCallback;
    }

    public void setServerCallback(final IServerCallback smppCallback) {
        this.serverCallback = smppCallback;
    }


    @Override
    public ISmppMessageHandler getMessageHandler() {
        return this.messageHandler;
    }

    @Override
    public SocketChannel getSocketChannel() {
        return this.socketChannel;
    }

    public void setSocketChannel(final SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }


    @Override
    public int getMaxThreadCount() {
        return this.getSmppApiDelegator().getApiProperties().getMaxThreadCount();
    }

    @Override
    public boolean handleCloseConnection() {
        this.closeConnection("Connection is closed");

        return true;
    }

    @Override
    public void handleTimeoutRequests() {

    }

    @Override
    public boolean isTraceON() {
        return this.getSmppApiDelegator().getApiProperties().isTraceOn();
    }

    public SmppConnectionType getBindType() {
        return this.bindType;
    }

    public void setBindType(final int bindType) {
        switch (bindType) {
            case Smpp34Constants.MSG_BIND_RECEIVER:
                this.bindType = SmppConnectionType.READ;
                break;
            case Smpp34Constants.MSG_BIND_TRANSCVR:
                this.bindType = SmppConnectionType.BOTH;
                break;
            case Smpp34Constants.MSG_BIND_TRANSMITTER:
                this.bindType = SmppConnectionType.WRITE;
                break;
            default:
                this.bindType = SmppConnectionType.BOTH;
                break;
        }
    }


    @Override
    public void handleNack(final BufferBean bufferBean,
                           final MessageHeader smpp34Header) {
        if (bufferBean.getWaitingObject().getMesssageType() == Smpp34Constants.MSG_DELIVER_SM) {
            if (bufferBean.getWaitingObject() != null) {
                final DeliverSMMessage deliverSM = (DeliverSMMessage) bufferBean.getWaitingObject();
                deliverSM.setSendResult(ESendResult.FATAL_ERROR);
                deliverSM.getMessageProcessor().errorReceived();
                if (deliverSM.getWaitObject() == null) {
                    this.getServerCallback().deliveryResult(this.getConnectionInformation(), deliverSM, deliverSM.getAttachedObject());
                } else {
                    synchronized (deliverSM.getWaitObject()) {
                        deliverSM.getWaitObject().notify();
                    }
                }
            }
        }
    }

    public String getServerName() {
        return this.serverName;
    }

    public ServerApiDelegator getSmppApiDelegator() {
        return this.smppApiDelegator;
    }

    public void setSmppApiDelegator(final ServerApiDelegator smppApiDelegator) {
        this.smppApiDelegator = smppApiDelegator;
    }

    public String getExternalConnectionName() {
        return this.externalConnectionName;
    }

    public void setExternalConnectionName(final String externalConnectionName) {
        this.externalConnectionName = externalConnectionName;
    }


    @Override
    public boolean isBinded() {
        return this.getBinded().get();
    }


    @Override
    public void adjustTps(final int newTps) {

    }


    @Override
    public void suspend() throws Exception {

    }


    @Override
    public void unSuspend() throws Exception {

    }


    @Override
    public boolean increaseTps(final int count) {
        return true;
    }


    @Override
    public void restart() throws Exception {

    }

}
