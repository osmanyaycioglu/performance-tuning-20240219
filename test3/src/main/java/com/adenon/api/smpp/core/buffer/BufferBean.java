package com.adenon.api.smpp.core.buffer;

import java.util.concurrent.atomic.AtomicInteger;

import com.adenon.api.smpp.message.MessageObject;


public class BufferBean {

    public static final int OBJECT_STATUS_WRITABLE = 0;
    public static final int OBJECT_STATUS_READABLE = 1;
    public static final int OBJECT_STATUS_IN_USE   = 2;
    private int             sequenceNumber;
    private long            useDate;
    private MessageObject   waitingObject;
    private AtomicInteger   status                 = new AtomicInteger(BufferBean.OBJECT_STATUS_WRITABLE);

    public BufferBean() {
    }

    public void release() {
        this.setWaitingObject(null);
        this.getStatus().set(BufferBean.OBJECT_STATUS_WRITABLE);
        this.setSequenceNumber(0);
        this.setUseDate(0);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(100);
        builder.append(" Sequence : ");
        builder.append(this.getSequenceNumber());
        builder.append(" Date : ");
        builder.append(this.getUseDate());
        builder.append(" Object info : ");
        if (this.getWaitingObject() != null) {
            builder.append(this.getWaitingObject().toString());
        }
        return builder.toString();
    }

    public int getSequenceNumber() {
        return this.sequenceNumber;
    }

    public void setSequenceNumber(final int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public long getUseDate() {
        return this.useDate;
    }

    public void setUseDate(final long useDate) {
        this.useDate = useDate;
    }

    public MessageObject getWaitingObject() {
        return this.waitingObject;
    }

    public void setWaitingObject(final MessageObject waitingObject) {
        this.waitingObject = waitingObject;
    }

    public AtomicInteger getStatus() {
        return this.status;
    }

    public void setStatus(final AtomicInteger status) {
        this.status = status;
    }
}
