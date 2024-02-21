package com.adenon.api.smpp.buffer;

import java.nio.ByteBuffer;

public class SendBufferObject {

    public static final int STATUS_FREE   = 0;
    public static final int STATUS_IN_USE = 1;

    private ByteBuffer      byteBuffer;
    private int             status;

    public SendBufferObject() {
        this.setByteBuffer(ByteBuffer.allocateDirect(SmppBufferManager.MAX_PDU_SIZE));
    }

    public ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }

    public void setByteBuffer(final ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }
}
