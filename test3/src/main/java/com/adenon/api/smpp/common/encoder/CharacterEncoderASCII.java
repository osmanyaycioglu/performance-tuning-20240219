package com.adenon.api.smpp.common.encoder;


public class CharacterEncoderASCII extends CharacterEncoderImpl {

    private static CharSet[] turkishToAsciiCharSet = new CharSet[] {
            new CharSet(new byte[] { (byte) 'g' }, (char) 0x11f),
            new CharSet(new byte[] { (byte) 'G' }, (char) 0x11e),
            new CharSet(new byte[] { (byte) 'u' }, (char) 0x00FC),
            new CharSet(new byte[] { (byte) 'U' }, (char) 0x00DC),
            new CharSet(new byte[] { (byte) 's' }, (char) 0x15f),
            new CharSet(new byte[] { (byte) 'S' }, (char) 0x15e),
            new CharSet(new byte[] { (byte) 'i' }, (char) 0x131),
            new CharSet(new byte[] { (byte) 'I' }, (char) 0x130),
            new CharSet(new byte[] { (byte) 'o' }, (char) 0x00F6),
            new CharSet(new byte[] { (byte) 'O' }, (char) 0x00D6),
            new CharSet(new byte[] { (byte) 'c' }, (char) 0x00e7),
            new CharSet(new byte[] { (byte) 'C' }, (char) 0x00C7) };


    public CharacterEncoderASCII() {
    }


    @Override
    public byte[] encode(final String str) {
        if (str.length() < 1) {
            return new byte[0];
        }
        final byte[] bytes = new byte[str.length()];
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);
            if (c < 128) {
                bytes[i] = (byte) c;
            } else {
                final byte[] trBytes = this.applyFilter(c, CharacterEncoderASCII.turkishToAsciiCharSet);
                bytes[i] = trBytes[0];
            }
        }
        return bytes;

    }

}
