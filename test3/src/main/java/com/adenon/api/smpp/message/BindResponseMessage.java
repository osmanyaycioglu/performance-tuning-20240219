package com.adenon.api.smpp.message;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.common.CommonUtils;


public class BindResponseMessage {

    private String systemId;

    public void fillBody(final ByteBuffer byteBuffer) throws Exception {
        if (this.getSystemId() != null) {
            byteBuffer.put(this.getSystemId().getBytes("ASCII"));
            byteBuffer.put((byte) 0);
        } else {
            byteBuffer.put((byte) 0);
        }
    }

    public void parseMessage(final ByteBuffer byteBuffer) throws Exception {
        byte[] temp = new byte[16];
        CommonUtils.getCOctetString(temp, byteBuffer);
        this.systemId = new String(temp);
        temp = null;

    }

    public String getSystemId() {
        return this.systemId;
    }

    public void setSystemId(final String systemId) {
        this.systemId = systemId;
    }
}
