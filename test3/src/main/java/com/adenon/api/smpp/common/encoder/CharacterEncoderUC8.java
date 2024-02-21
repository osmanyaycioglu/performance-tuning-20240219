package com.adenon.api.smpp.common.encoder;


public class CharacterEncoderUC8 extends CharacterEncoderImpl {

    @Override
    public byte[] encode(final String str) {
        if (str.length() < 1) {
            return new byte[0];
        }
        final byte[] bytes = new byte[str.length() * 2];
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);
            bytes[i * 2] = (byte) ((c & 0xff00) >> 8);
            bytes[(i * 2) + 1] = (byte) (c & 0x00ff);
        }
        return bytes;
    }

}
