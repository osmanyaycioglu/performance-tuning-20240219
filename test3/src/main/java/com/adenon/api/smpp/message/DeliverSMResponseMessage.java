package com.adenon.api.smpp.message;

import java.nio.ByteBuffer;

public class DeliverSMResponseMessage {

    public DeliverSMResponseMessage() {
    }

    public void fillBody(final ByteBuffer byteBuffer) throws Exception {
        byteBuffer.put((byte) 0);
    }
}
