package com.adenon.api.smpp.sdk;


public class RawMessage implements IRawMessage {

    private final byte[] bytes;

    public RawMessage(final byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public byte[] getBytes() {
        return this.bytes;
    }

}
