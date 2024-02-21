package com.adenon.api.smpp.common.encoder;


public class CharSet {

    private byte[] equalBytes;
    private char   oldChar;

    public CharSet(final byte[] equBytes,
                   final char character) {
        this.equalBytes = equBytes;
        this.oldChar = character;
    }

    public char getOldChar() {
        return this.oldChar;
    }

    public void setOldChar(final char oldChar) {
        this.oldChar = oldChar;
    }

    public byte[] getEqualBytes() {
        return this.equalBytes;
    }

    public void setEqualBytes(final byte[] equalBytes) {
        this.equalBytes = equalBytes;
    }
}
