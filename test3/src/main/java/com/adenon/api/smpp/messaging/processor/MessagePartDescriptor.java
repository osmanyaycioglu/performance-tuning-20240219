package com.adenon.api.smpp.messaging.processor;

public class MessagePartDescriptor {

    private int    start;
    private int    end;
    private int    length;
    private byte[] byteArray;

    public int getStart() {
        return this.start;
    }

    public void setStart(final int start) {
        this.start = start;
    }

    public int getEnd() {
        return this.end;
    }

    public void setEnd(final int end) {
        this.end = end;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(final int length) {
        this.length = length;
    }

    public byte[] getByteArray() {
        return this.byteArray;
    }

    public void setByteArray(final byte[] byteArray) {
        this.byteArray = byteArray;
    }
}
