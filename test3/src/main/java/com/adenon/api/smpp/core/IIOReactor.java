package com.adenon.api.smpp.core;

import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import com.adenon.api.smpp.core.buffer.BufferBean;
import com.adenon.api.smpp.core.buffer.ResponseBufferImplementation;
import com.adenon.api.smpp.logging.LoggerWrapper;
import com.adenon.api.smpp.message.MessageHeader;
import com.adenon.api.smpp.sdk.ConnectionInformation;


public interface IIOReactor {

    void restart() throws Exception;

    void shutdown() throws Exception;

    void suspend() throws Exception;

    void unSuspend() throws Exception;

    void closeConnection(String description);

    void setLogger(LoggerWrapper _logger);

    LoggerWrapper getLogger();

    String getLabel();

    ISmppMessageHandler getMessageHandler();

    long getLastWriteTime();

    void setLastWriteTime(long lastActivityTime);

    ConnectionInformation getConnectionInformation();

    long getLastReadTime();

    void setLastReadTime(long lastReadTime);

    void setStartWriteTime(long startWriteTime);

    void setSuspendStartTime(long suspendStartTime);

    Object getReadLock();

    SocketChannel getSocketChannel();

    StatisticCollector getStatisticCollector();

    int getThreadCount();

    void increaseThreadCount();

    int getMaxThreadCount();

    SmppPackageReader getSmppPackageReader();

    void decreaseThreadCount();

    boolean handleCloseConnection();

    boolean isWriteEnd();

    ResponseBufferImplementation getResponseBuffer();

    void sendAlive(int seq) throws Exception;

    int getSequenceNumber();

    long getStartWriteTime();

    long getSuspendStartTime();

    long getSuspendEndTime();

    void setSuspendEndTime(long endTime);

    long getThrottleEndTime();

    void setThrottleEndTime(long endTime);

    void handleTimeoutRequests();

    boolean isTraceON();

    ConnectionController getConnectionController();

    void sendAliveRes(int sequenceNo) throws Exception;

    void handleNack(BufferBean bufferBean,
                    MessageHeader smpp34Header);

    boolean isBinded();

    void adjustTps(int newTps);

    public long getThrottleStartTime();

    public void setThrottleStartTime(final long throttleStartTime);

    public void setAttachment(Object attachment);

    public Object getAttachment();

    AtomicBoolean getBinded();

}