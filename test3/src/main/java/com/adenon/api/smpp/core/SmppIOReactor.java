package com.adenon.api.smpp.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import com.adenon.api.smpp.buffer.SendBufferObject;
import com.adenon.api.smpp.buffer.SmppBufferManager;
import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.common.SequenceGenerator;
import com.adenon.api.smpp.common.Smpp34Constants;
import com.adenon.api.smpp.common.SmppApiException;
import com.adenon.api.smpp.common.StateHigherAuthority;
import com.adenon.api.smpp.common.TransactionManager;
import com.adenon.api.smpp.core.buffer.BufferBean;
import com.adenon.api.smpp.core.buffer.ResponseBufferImplementation;
import com.adenon.api.smpp.core.handler.SmppMessageHandler;
import com.adenon.api.smpp.logging.LoggerWrapper;
import com.adenon.api.smpp.message.BindRequestMessage;
import com.adenon.api.smpp.message.DeliverSMResponseMessage;
import com.adenon.api.smpp.message.MessageHeader;
import com.adenon.api.smpp.message.Smpp34CancelSM;
import com.adenon.api.smpp.message.Smpp34QuerySM;
import com.adenon.api.smpp.message.SubmitMultiSMMessage;
import com.adenon.api.smpp.message.SubmitSMMessage;
import com.adenon.api.smpp.message.SubmitSMResponseMessage;
import com.adenon.api.smpp.sdk.AddressDescriptor;
import com.adenon.api.smpp.sdk.AlarmCode;
import com.adenon.api.smpp.sdk.ConnectionDescriptor;
import com.adenon.api.smpp.sdk.ConnectionInformation;
import com.adenon.api.smpp.sdk.ESendResult;
import com.adenon.api.smpp.sdk.IRawMessage;
import com.adenon.api.smpp.sdk.IRawMessages;
import com.adenon.api.smpp.sdk.ISmppCallback;
import com.adenon.api.smpp.sdk.SmppConnectionType;
import com.adenon.library.common.utils.tps.NonBlockingTpsCounter;

public class SmppIOReactor extends IOReactor {

    public final static long          MAX_NUMBER     = 99999999999999L;
    public final static long          MIN_NUMBER     = 1;

    private final ISmppMessageHandler messageHandler = new SmppMessageHandler(this);
    private int                       order          = 0;
    private ISmppCallback             smppCallback;

    private SocketChannel             socketChannel  = null;

    private static long               ackNumber      = 0;

    private ConnectionDescriptor      connectionDescriptor;
    private final SmppApiDelegator    smppApiDelegator;

    private boolean                   userSuspended;

    public SmppIOReactor(final LoggerWrapper logger,
                         final String connectionGroupName,
                         final ConnectionDescriptor connectionDescriptor,
                         final StateHigherAuthority stateHigherAuthority,
                         final SmppApiDelegator smppApiDelegator) throws Exception {
        super(logger);
        connectionDescriptor.validate();
        this.smppApiDelegator = smppApiDelegator;

        final ConnectionInformation conInformation = new ConnectionInformation(this, connectionGroupName, connectionDescriptor.getConnectionName());
        this.setConnectionInformation(conInformation);
        if (connectionDescriptor.getTps() > 0) {
            this.getConnectionInformation().setNonBlockingTpsCounter(new NonBlockingTpsCounter(connectionDescriptor.getTps()));
        }
        conInformation.setTps(connectionDescriptor.getTps());
        stateHigherAuthority.addState(this.getConnectionInformation().getConnectionState());
        this.setIoReactorLock(new Object());
        this.setShutdown(false);
        this.connectionDescriptor = connectionDescriptor.getACopy();
        connectionDescriptor.setConnectionGroupName(connectionGroupName);
        this.setLabel(CommonUtils.getClientHostLabel(connectionGroupName, this.connectionDescriptor.getConnectionName()));

    }

    public int sendCancelSm(final String messageId,
                            final AddressDescriptor sourceAddress,
                            final AddressDescriptor destinationAddress) throws Exception {

        final Smpp34CancelSM cancelSM = new Smpp34CancelSM(this.getLogger());
        cancelSM.setParamMessageId(messageId);
        cancelSM.setSourceAddress(sourceAddress);
        cancelSM.setDestinationAddress(destinationAddress);

        final SendBufferObject bufferObject = SmppBufferManager.getNextBufferObject();
        if (bufferObject == null) {
            throw new SmppApiException(SmppApiException.FATAL_ERROR, "No send buffer");
        }
        try {

            final int sequenceNumber = this.getSequenceNumber();
            cancelSM.fillBody(bufferObject.getByteBuffer(), sequenceNumber);
            this.sendMsg(cancelSM, sequenceNumber, bufferObject.getByteBuffer());
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("Smpp34CancelSM",
                                       "send",
                                       0,
                                       null,
                                       " : -----> |-*Sending CANCEL SMS*-| SequenceID : " + sequenceNumber + " msgID : " + messageId);
            }
            return sequenceNumber;
        } catch (final Exception e) {
            this.getLogger().error("SmppIOReactor", "sendCancelSm", 0, null, " : Error : " + e.getMessage(), e);
            throw e;
        } finally {
            SmppBufferManager.releaseBufferObject(bufferObject);
        }
    }

    public int sendQuerySm(final String messageId,
                           final AddressDescriptor sourceAddress) throws Exception {

        final Smpp34QuerySM querySM = new Smpp34QuerySM(this.getLogger());
        querySM.setParamMessageId(messageId);
        querySM.setSourceAddress(sourceAddress);

        final SendBufferObject bufferObject = SmppBufferManager.getNextBufferObject();
        final int sequenceNumber = this.getSequenceNumber();
        querySM.fillBody(bufferObject.getByteBuffer(), sequenceNumber);
        this.sendMsg(querySM, sequenceNumber, bufferObject.getByteBuffer());
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("Smpp34QuerySM",
                                   "send",
                                   0,
                                   null,
                                   " : -----> |-*Sending QUERY SMS*-| SequenceID : " + sequenceNumber + " msgID : " + messageId);
        }
        return sequenceNumber;
    }

    public void initialize() throws SmppApiException {
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger()
                .debug("SmppIOReactor", "initialize", 0, this.getLabel(), "IO Reactor is initializing. --> " + this.connectionDescriptor.toString());
        }
        this.setResponseBuffer(new ResponseBufferImplementation(this.connectionDescriptor.getSmppWindowSize(), 2, this.getLogger(), 3000, this.getLabel()));
        if ((this.connectionDescriptor.getUsername().length() > 16) || (this.connectionDescriptor.getPassword().length() > 9)) {
            this.getLogger().error("SmppIOReactor", "initialize", 0, this.getLabel(), " : Error : Invalid input. Please check user and password length!!!");
            throw new SmppApiException(SmppApiException.PROTOCOL_ERROR, "invalid param length in userid, password or label params");
        }

        this.setSmppCallback(this.getConnectionDescriptor().getCallbackInterface());

        this.setConnectionController(new ConnectionController(this));
        this.getConnectionController().setDaemon(true);
        this.getConnectionController().start();

    }

    public String getHostName() {
        return this.getConnectionDescriptor().getConnectionName();
    }

    /**
     * if connected, send unbind command to host
     * 
     */
    public void sendUnbind() {

        if (this.getConnectionInformation().isConnected()) {

            this.sendLogoutMsg();

            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("SmppIOReactor", "closeCon", 0, this.getLabel(), "Unbinding user from host.");
            }
        }
    }

    /**
     * set connection state as suspended and send unbind message
     * 
     */
    private void sendLogoutMsg() {
        this.getBinded().set(true);
        this.getConnectionInformation().getConnectionState().suspended();
        this.unbindTransceiver();
    }

    /**
     * create sequence number fill buffer with createAckHeader method (put buffer command length, command id, command status, sequence number)
     * 
     */
    public void unbindTransceiver() {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        final int sequenceNumber = SequenceGenerator.getNextSequenceNum();
        CommonUtils.createAckHeader(byteBuffer, Smpp34Constants.MSG_UNBIND, sequenceNumber, 0);
        try {
            this.writeBuffer(byteBuffer);
        } catch (final IOException e) {
            this.getLogger().error("SmppIOReactor", "unbindTransceiver", 0, null, " : Error : " + e.getMessage(), e);
        }
    }

    /**
     * @param sequenceNumber
     * 
     *            if connected, send logoutResponse message
     * 
     */
    public void sendUnbindResponse(final int sequenceNumber) {

        if (this.getConnectionInformation().isConnected()) {

            this.sendLogoutResponseMsg(sequenceNumber);

            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("SmppIOReactor", "sendUnbindResponse", 0, this.getLabel(), "User unbound from host. (host request)");
            }
        }

    }

    /**
     * @param sequenceNumber
     * 
     *            set connection state as suspended and send unbind response message
     * 
     */
    private void sendLogoutResponseMsg(final int sequenceNumber) {
        this.getBinded().set(true);
        this.getConnectionInformation().getConnectionState().suspended();
        this.unbindResponseTransceiver(sequenceNumber);

    }

    /**
     * @param sequenceNumber
     * 
     *            fill buffer with createAckHeader method (put buffer command length, command id, command status, sequence number)
     * 
     *            use the sequence number of unbind request message
     * 
     */
    private void unbindResponseTransceiver(final int sequenceNumber) {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        CommonUtils.createAckHeader(byteBuffer, Smpp34Constants.MSG_UNBIND_RESP, sequenceNumber, 0);
        try {
            this.writeBuffer(byteBuffer);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void openConnectionAndSendLogin() throws Exception {
        this.openConnections();
        if (this.getConnectionInformation().isConnected()) {
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("SmppIOReactor", "openCon", 0, this.getLabel(), " IO Reactor initiated the connection.");
            }
            this.sendLoginMsg();
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("SmppIOReactor", "openCon", 0, this.getLabel(), "Binding user to host.");
            }
        }
    }

    public void openConnections() throws IOException {
        boolean done = false;
        int i = 0;
        while (!this.isShutdown() && !done && (i < 6)) {
            this.getConnectionInformation().setIp(this.getConnectionDescriptor().getIpList().get(this.order));
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("SmppIOReactor",
                                       "openConnections",
                                       0,
                                       this.getLabel(),
                                       " : "
                                               + this.getConnectionDescriptor().getConnectionName()
                                               + " connection will be opened to Ip : "
                                               + this.getConnectionInformation().getIp()
                                               + " Port : "
                                               + this.getConnectionDescriptor().getPort()
                                               + " order : "
                                               + this.order
                                               + " try count "
                                               + (i + 1));
            }
            synchronized (this.getIoReactorLock()) {
                try {
                    this.setSocketChannel(SocketChannel.open());
                    this.getSocketChannel()
                        .socket()
                        .connect(new InetSocketAddress(this.getConnectionInformation().getIp(), this.getConnectionDescriptor().getPort()),
                                 this.getConnectionDescriptor().getConnectionTimeout());
                    this.getSocketChannel().socket().setKeepAlive(true);
                    this.getSocketChannel().configureBlocking(true);
                    if (!this.getSocketChannel().isConnected()) {
                        throw new IOException("Not connected");
                    }
                    done = true;
                    this.smppCallback.connected(this.getConnectionInformation());
                    this.getConnectionInformation().setConnected(true);
                    this.getConnectionInformation().setConnectionLabel("|"
                                                                       + this.getConnectionDescriptor().getConnectionGroupName()
                                                                       + "|"
                                                                       + this.getConnectionDescriptor().getConnectionName()
                                                                       + "|"
                                                                       + this.getConnectionInformation().getIp()
                                                                       + ":"
                                                                       + this.getConnectionDescriptor().getPort()
                                                                       + "|");
                    this.getConnectionInformation().getConnectionState().suspended();
                    break;
                } catch (final IOException ioException) {
                    this.smppCallback.alarm(this.getConnectionInformation(), AlarmCode.IOError, ioException.getMessage());
                    this.getLogger().error("SmppIOReactor", "openConnections", 0, null, " : Error : " + ioException.getMessage(), ioException);
                    if (this.getConnectionDescriptor().getIpList().size() > 1) {
                        this.incConnectionOrder();
                    }
                    this.cleanupConnection(ioException.getMessage());
                }
            }

            i++;
            try {
                Thread.sleep(500L);
            } catch (final Exception e) {
            }
        }
    }

    public void incConnectionOrder() {
        this.order++;
        this.order = this.order % this.getConnectionDescriptor().getIpList().size();
    }

    public void cleanupConnection(final String reason) {
        try {
            try {
                this.getConnectionInformation().getConnectionState().stopped();
            } catch (final Exception e) {
            }
            try {
                this.unbindTransceiver();
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
            this.getLogger().error("SmppIOReactor", "closeConnection", 0, null, " : Error : " + e.getMessage(), e);
        } finally {
            try {
                this.getConnectionInformation().setConnected(false);
                this.setSocketChannel(null);
                this.getConnectionInformation().getConnectionState().stopped();
                this.getResponseBuffer().resetBuffer();
                this.smppCallback.disconnected(this.getConnectionInformation());
            } catch (final Exception e) {
                this.getLogger().error("SmppIOReactor", "closeConnection", 0, null, " : Error : " + e.getMessage(), e);
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
                    this.getLogger().info("SmppIOReactor", "closeConnection", 0, this.getLabel(), "#CLOSING CONNECTION# Description : " + description);
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
                        if (this.getResponseBuffer().getBufferBeans()[i].getWaitingObject().getMesssageType() == Smpp34Constants.MSG_SUBMIT_SM) {
                            final SubmitSMMessage submitSM = (SubmitSMMessage) this.getResponseBuffer().getBufferBeans()[i].getWaitingObject();
                            submitSM.setSendResult(ESendResult.RETRY);
                            if (submitSM.getWaitObject() == null) {
                                this.smppCallback.submitResult(this.getConnectionInformation(), submitSM, submitSM.getAttachedObject());
                            } else {
                                synchronized (submitSM.getWaitObject()) {
                                    submitSM.getWaitObject().notify();
                                }
                            }
                        }
                    }
                }
            } catch (final Exception e) {
                this.getLogger().error("SmppIOReactor", "nackToWaitingObjects", 0, null, " : Error : " + e.getMessage(), e);
            }
            try {
                this.getResponseBuffer().getBufferBeans()[i].release();
            } catch (final Exception e) {
                this.getLogger().error("SmppIOReactor", "nackToWaitingObjects", 0, null, " : Error : " + e.getMessage(), e);
            }
        }
    }

    private void sendLoginMsg() throws Exception {
        this.getBinded().set(false);
        this.getConnectionInformation().getConnectionState().suspended();
        try {
            this.bindTransceiver();
        } catch (final Exception e) {
            this.incConnectionOrder();
            throw e;
        }
    }

    public void bindTransceiver() throws Exception {
        final SendBufferObject nextBufferObject = SmppBufferManager.getNextBufferObject();
        if (nextBufferObject == null) {
            throw new SmppApiException(SmppApiException.FATAL_ERROR, "No send buffer");
        }
        try {
            final ByteBuffer byteBuffer = nextBufferObject.getByteBuffer();
            final BindRequestMessage bindRequestMessage = new BindRequestMessage();
            bindRequestMessage.setSystemIdentifier(this.getConnectionDescriptor().getUsername());
            bindRequestMessage.setPassword(this.getConnectionDescriptor().getPassword());
            bindRequestMessage.setConnectionType(this.getConnectionDescriptor().getConnectionType());
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("SmppIOReactor",
                                       "bindTransceiver",
                                       0,
                                       null,
                                       " : Binding username : "
                                               + this.getConnectionDescriptor().getUsername()
                                               + " password : "
                                               + this.getConnectionDescriptor().getPassword()
                                               + " interfaceVersion : "
                                               + Smpp34Constants.INTERFACE_VERSION);
            }
            final int sequenceNumber = SequenceGenerator.getNextSequenceNum();
            bindRequestMessage.fillBody(byteBuffer, sequenceNumber);
            this.writeBuffer(byteBuffer);
        } catch (final Exception e) {
            this.getLogger().error("SmppIOReactor", "bindTransceiver", 0, null, " : Error : " + e.getMessage(), e);
        } finally {
            SmppBufferManager.releaseBufferObject(nextBufferObject);
        }
    }

    public void sendSubmitSMResponse(final int seqno,
                                     final ByteBuffer byteBuffer,
                                     final int status) throws Exception {
        final String msgRef = SmppIOReactor.getNextAckNum() + "/" + System.currentTimeMillis();
        final SubmitSMResponseMessage submitSMResponse = new SubmitSMResponseMessage();
        submitSMResponse.setMessageIdentifier(msgRef);
        byteBuffer.clear();
        CommonUtils.createHeader(byteBuffer, Smpp34Constants.MSG_SUBMIT_SM_RESP, seqno, status);
        submitSMResponse.fillBody(byteBuffer);
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

    public synchronized static long getNextAckNum() {
        SmppIOReactor.ackNumber++;
        if (SmppIOReactor.ackNumber > SmppIOReactor.MAX_NUMBER) {
            SmppIOReactor.ackNumber = SmppIOReactor.MIN_NUMBER;
        }
        return SmppIOReactor.ackNumber;
    }

    public SubmitSMMessage createSubmitSMMessage() {
        final SubmitSMMessage smpp34SubmitSM = new SubmitSMMessage(this.getLogger(), TransactionManager.getNextTransactionID(), this.getLabel());
        return smpp34SubmitSM;
    }

    public SubmitSMMessage createSubmitSMMessage(final long transactionId) {
        final SubmitSMMessage smpp34SubmitSM = new SubmitSMMessage(this.getLogger(), transactionId, this.getLabel());
        return smpp34SubmitSM;
    }

    public SubmitMultiSMMessage createSubmitMultiSMMessage() {
        final SubmitMultiSMMessage smpp34SubmitMultiSM = new SubmitMultiSMMessage(this.getLogger(), TransactionManager.getNextTransactionID(), this.getLabel());
        return smpp34SubmitMultiSM;
    }

    public SubmitMultiSMMessage createSubmitMultiSMMessage(final int transactionId) {
        final SubmitMultiSMMessage smpp34SubmitMultiSM = new SubmitMultiSMMessage(this.getLogger(), transactionId, this.getLabel());
        return smpp34SubmitMultiSM;
    }

    @Override
    public void restart() {
        this.closeConnection("Restart command received.");
    }

    public String getConnectionName() {
        return this.getConnectionDescriptor().getConnectionName();
    }

    public int getWindowSize() {
        return this.getResponseBuffer().getBufferSize();
    }

    public int getSmsType(final int datacoding) {
        return 0;
    }
    
    private static ArrayList<SubmitSMMessage> submitSM = new ArrayList<>();

    public long sendSubmitSM(final SubmitSMMessage smpp34SubmitSM,
                             final boolean putBinaryHeader,
                             final boolean isRequestDelivery,
                             final Object returnObject) throws Exception {

        smpp34SubmitSM.init(putBinaryHeader, returnObject);
        submitSM.add(smpp34SubmitSM);
        final int messageCount = smpp34SubmitSM.getMessageProcessor().getMessagePartCount();
        if (messageCount > 0) {
            final int referenceNumber = this.getNextRefNumByte();
            for (int i = 0; i < messageCount; i++) {
                this.smppApiDelegator.getBlockingTpsCounter().increase();
                byte[] concatHeader = null;
                final int sequenceNumber = this.getSequenceNumber();
                if (messageCount > 1) {
                    if (putBinaryHeader) {
                        smpp34SubmitSM.setParamESMClass(64);
                        concatHeader = smpp34SubmitSM.getConcatHeader(messageCount, i + 1, referenceNumber);
                    } else {
                        smpp34SubmitSM.setOpParamSarMsgRefNum(referenceNumber);
                        smpp34SubmitSM.setOpParamSarSegmentSequenceNum(i + 1);
                        smpp34SubmitSM.setOpParamSarTotalSegments(messageCount);
                    }
                }
                final SendBufferObject nextBufferObject = SmppBufferManager.getNextBufferObject();
                if (nextBufferObject == null) {
                    throw new SmppApiException(SmppApiException.FATAL_ERROR, "No send buffer");
                }
                try {
                    if (isRequestDelivery) {
                        if (i == (messageCount - 1)) {
                            smpp34SubmitSM.setParamRegisteredDelivery(1);
                        } else {
                            smpp34SubmitSM.setParamRegisteredDelivery(0);
                        }
                    } else {
                        smpp34SubmitSM.setParamRegisteredDelivery(0);
                    }
                    smpp34SubmitSM.getMessageProcessor().addSequence(i, sequenceNumber);
                    smpp34SubmitSM.fillBuffer(nextBufferObject.getByteBuffer(), sequenceNumber, concatHeader, i);
                    this.sendMsg(smpp34SubmitSM, sequenceNumber, nextBufferObject.getByteBuffer());
                    smpp34SubmitSM.setSendResult(ESendResult.SUCCESS);
                } finally {
                    SmppBufferManager.releaseBufferObject(nextBufferObject);
                }
            }
        }
        return smpp34SubmitSM.getTransID();
    }

    public long sendSubmitSMBytes(final SubmitSMMessage smpp34SubmitSM,
                                  final Object returnObject) throws Exception {

        this.smppApiDelegator.getBlockingTpsCounter().increase();
        final SendBufferObject nextBufferObject = SmppBufferManager.getNextBufferObject();
        if (nextBufferObject == null) {
            throw new SmppApiException(SmppApiException.FATAL_ERROR, "No send buffer");
        }
        try {
            smpp34SubmitSM.setAttachedObject(returnObject);
            final IRawMessages rawMessages = smpp34SubmitSM.getRawMessages();
            if (rawMessages == null) {
                throw new SmppApiException(SmppApiException.FATAL_ERROR, "Raw message can not be null while sending raw smpp message!");
            }
            final List<IRawMessage> messageList = rawMessages.getMessageList();
            int index = 0;
            for (final IRawMessage iRawMessage : messageList) {
                final int sequenceNumber = this.getSequenceNumber();
                smpp34SubmitSM.getMessageProcessor().addSequence(index, sequenceNumber);
                index++;
                final ByteBuffer byteBuffer = nextBufferObject.getByteBuffer();
                byteBuffer.clear();
                // byteBuffer.position(4);
                byteBuffer.put(iRawMessage.getBytes());
                byteBuffer.flip();
                smpp34SubmitSM.fillHeader(byteBuffer, sequenceNumber);
                // byteBuffer.position(0);
                // byteBuffer.putInt(byteBuffer.limit());
                // System.out.println(CommonUtils.bytesToHexFormated(byteBuffer, 0, byteBuffer.limit()));
                byteBuffer.position(byteBuffer.limit());
                this.sendMsg(smpp34SubmitSM, sequenceNumber, byteBuffer);
            }
            smpp34SubmitSM.setSendResult(ESendResult.SUCCESS);
        } finally {
            SmppBufferManager.releaseBufferObject(nextBufferObject);
        }
        return smpp34SubmitSM.getTransID();
    }

    @Override
    public boolean handleCloseConnection() {
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("SmppIOReactor",
                                   "handleCloseConnection",
                                   0,
                                   this.getLabel(),
                                   " : " + this.getConnectionDescriptor().getConnectionName() + " Handling Connection... ");
        }

        if (!this.isShutdown() && !this.userSuspended) {
            try {
                Thread.sleep(1000L);
            } catch (final InterruptedException e) {
            }
            try {
                if (this.getLogger().isDebugEnabled()) {
                    this.getLogger().debug("SmppIOReactor",
                                           "handleCloseConnection",
                                           0,
                                           this.getLabel(),
                                           " : " + this.getConnectionDescriptor().getConnectionName() + " Reopening connection ...");
                }
                this.openConnectionAndSendLogin();
            } catch (final Exception ex) {
                this.getLogger().error("ConnectionController", "run", 0, null, " : Error : " + ex.getMessage(), ex);
                this.closeConnection(ex.getMessage());
            }
            return false;
        } else {
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("SmppIOReactor",
                                       "handleCloseConnection",
                                       0,
                                       this.getLabel(),
                                       " : " + this.getConnectionDescriptor().getConnectionName() + " Shutdown initated. Clean up connection = true... ");
            }
            return true;
        }
    }

    @Override
    public void handleTimeoutRequests() {
        final TimeoutConsumerThread timeoutConsumerThread = new TimeoutConsumerThread(this, this.getResponseBuffer().getTimeoutQueue());
        timeoutConsumerThread.start();

    }

    public SmppConnectionType getConnectionType() {
        return this.getConnectionDescriptor().getConnectionType();
    }

    public int getUsedBufferCount() {
        return this.getResponseBuffer().getUsedItemCount();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        return builder.toString();
    }

    public ISmppCallback getSmppCallback() {
        return this.smppCallback;
    }

    public void setSmppCallback(final ISmppCallback smppCallback) {
        this.smppCallback = smppCallback;
    }

    @Override
    public ISmppMessageHandler getMessageHandler() {
        return this.messageHandler;
    }

    public ConnectionDescriptor getConnectionDescriptor() {
        return this.connectionDescriptor;
    }

    public void setConnectionDescriptor(final ConnectionDescriptor connectionDescriptor) {
        this.connectionDescriptor = connectionDescriptor;
    }

    @Override
    public SocketChannel getSocketChannel() {
        return this.socketChannel;
    }

    public void setSocketChannel(final SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void updateConnectionDescriptor(final ConnectionDescriptor connectionDescriptor) {
        if (this.connectionDescriptor.getTps() != connectionDescriptor.getTps()) {
            this.adjustTps(connectionDescriptor.getTps());
        }
        this.connectionDescriptor = connectionDescriptor.getACopy();

    }

    @Override
    public int getMaxThreadCount() {
        return this.connectionDescriptor.getMaxThreadCount();
    }

    @Override
    public boolean isTraceON() {
        return this.connectionDescriptor.isTraceON();
    }

    public boolean isUserSuspended() {
        return this.userSuspended;
    }

    @Override
    public void handleNack(final BufferBean bufferBean,
                           final MessageHeader smpp34Header) {
        if (bufferBean.getWaitingObject().getMesssageType() == Smpp34Constants.MSG_SUBMIT_SM) {
            if (bufferBean.getWaitingObject() != null) {
                final SubmitSMMessage sentSubmitSM = (SubmitSMMessage) bufferBean.getWaitingObject();
                sentSubmitSM.setSendResult(ESendResult.FATAL_ERROR);
                sentSubmitSM.getMessageProcessor().errorReceived();
                if (sentSubmitSM.getWaitObject() == null) {
                    this.getSmppCallback().submitResult(this.getConnectionInformation(), sentSubmitSM, sentSubmitSM.getAttachedObject());
                } else {
                    synchronized (sentSubmitSM.getWaitObject()) {
                        sentSubmitSM.getWaitObject().notify();
                    }
                }
            }
        } else if (bufferBean.getWaitingObject().getMesssageType() == Smpp34Constants.MSG_CANCEL_SM) {
            if (bufferBean.getWaitingObject() != null) {
                final Smpp34CancelSM smpp34CancelSM = (Smpp34CancelSM) bufferBean.getWaitingObject();
                this.getSmppCallback().cancelResult(this.getConnectionInformation(),
                                                    smpp34Header.getSequenceNo(),
                                                    smpp34Header.getCommandStatus(),
                                                    smpp34CancelSM.getParamMessageId());
            }
        }

    }

    @Override
    public boolean isBinded() {
        return this.getBinded().get();
    }

    @Override
    public boolean increaseTps(final int count) {
        if (this.getConnectionInformation().getNonBlockingTpsCounter() != null) {
            return this.getConnectionInformation().getNonBlockingTpsCounter().increaseBy(count);
        } else {
            return true;
        }
    }

    @Override
    public void adjustTps(final int newTps) {
        if (this.getConnectionInformation().getNonBlockingTpsCounter() != null) {
            this.getConnectionInformation().getNonBlockingTpsCounter().adjustTps(newTps);
            this.getConnectionInformation().setTps(newTps); // ???
        }
    }

    @Override
    public void shutdown() throws Exception {
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("SmppIOReactor", "shutdown", 0, this.getLabel(), "*SHUTDOWN* Initiated : " + this.toString());
        }
        this.getConnectionInformation().getConnectionState().suspended();
        this.setShutdown(true);
        try {
            Thread.sleep(900);
        } catch (final Exception e) {
        }
        if (this.getConnectionInformation().isConnected()) {
            this.unbindTransceiver();
        }
        this.closeConnection("Shutdown initiated");
        this.getConnectionInformation().getConnectionState().stopped();
    }

    @Override
    public void suspend() throws Exception {
        this.getConnectionInformation().getConnectionState().suspended();
        this.setSuspendEndTime(Long.MAX_VALUE);
        this.userSuspended = true;
    }

    @Override
    public void unSuspend() throws Exception {
        if (this.userSuspended) {
            this.userSuspended = false;
            this.setSuspendEndTime(System.currentTimeMillis());
        }
    }


}
