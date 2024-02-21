package com.adenon.api.smpp.wappush;

import java.nio.ByteBuffer;

public class WDPPart {

    private final short         destinationPort;
    private final short         sourcePort;

    // , (byte) 0x0B, (byte) 0x84, (byte) 0x23, (byte) 0xF0
    // (byte) 0x0B, (byte) 0x84, (byte) 0x23, (byte) 0xF0, (byte) 0x00, (byte) 0x03

    private final static byte[] udhBytes       = new byte[] { (byte) 0x06, (byte) 0x05, (byte) 0x04 };
    private final static byte[] udhConcatBytes = new byte[] { (byte) 0x0B, (byte) 0x05, (byte) 0x04 };


    public WDPPart(final short destinationPort,
                   final short sourcePort) {
        this.destinationPort = destinationPort;
        this.sourcePort = sourcePort;
    }

    public void encodeUDHBytes(final ByteBuffer byteBuffer) {
        byteBuffer.put(WDPPart.udhBytes);
        byteBuffer.putShort(this.destinationPort);
        byteBuffer.putShort(this.sourcePort);
    }

    public void encodeUDHBytes(final ByteBuffer byteBuffer,
                               final int msgCount,
                               final int msgIndex,
                               final int msgId) {
        byteBuffer.put(WDPPart.udhConcatBytes);
        byteBuffer.putShort(this.destinationPort);
        byteBuffer.putShort(this.sourcePort);
        byteBuffer.put((byte) 0x00);
        byteBuffer.put((byte) 0x03);
        byteBuffer.put((byte) (0xFF & msgId));
        byteBuffer.put((byte) (0xFF & msgCount));
        byteBuffer.put((byte) (0xFF & (msgIndex + 1)));
    }

    public final static int getUdhBytesLength() {
        return 7;
    }

    public final static int getUdhConcatBytesLength() {
        return 12;
    }

    public final static int getTotalBytesLength() {
        return 140;
    }

}
