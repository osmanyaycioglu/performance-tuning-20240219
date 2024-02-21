package com.adenon.api.smpp.common.encoder;

import java.nio.ByteBuffer;

public class CharacterEncoderGSM extends CharacterEncoderImpl {

    private static CharSet[] turkishToGSMCharSet = new CharSet[] {
            new CharSet(new byte[] { (byte) 'g' }, (char) 0x11f),
            new CharSet(new byte[] { (byte) 'G' }, (char) 0x11e),
            new CharSet(new byte[] { (byte) 0x7e }, (char) 0x00FC),
            new CharSet(new byte[] { (byte) 0x5e }, (char) 0x00DC),
            new CharSet(new byte[] { (byte) 's' }, (char) 0x15f),
            new CharSet(new byte[] { (byte) 'S' }, (char) 0x15e),
            new CharSet(new byte[] { (byte) 'i' }, (char) 0x131),
            new CharSet(new byte[] { (byte) 'I' }, (char) 0x130),
            new CharSet(new byte[] { (byte) 0x7c }, (char) 0x00F6),
            new CharSet(new byte[] { (byte) 0x5c }, (char) 0x00D6),
            new CharSet(new byte[] { (byte) 'c' }, (char) 0x00e7),
            new CharSet(new byte[] { (byte) 0x09 }, (char) 0x00C7),
            new CharSet(new byte[] { (byte) 0x01 }, (char) 0x00A3),
            new CharSet(new byte[] { (byte) 0x03 }, (char) 165),
            new CharSet(new byte[] { (byte) 0x04 }, (char) 232),
            new CharSet(new byte[] { (byte) 0x05 }, (char) 233),
            new CharSet(new byte[] { (byte) 0x06 }, (char) 249),
            new CharSet(new byte[] { (byte) 0x07 }, (char) 236),
            new CharSet(new byte[] { (byte) 0x08 }, (char) 242),
            new CharSet(new byte[] { (byte) 0x10 }, (char) 916),
            new CharSet(new byte[] { (byte) 0x12 }, (char) 934),
            new CharSet(new byte[] { (byte) 0x13 }, (char) 915),
            new CharSet(new byte[] { (byte) 0x14 }, (char) 923),
            new CharSet(new byte[] { (byte) 0x15 }, (char) 937),
            new CharSet(new byte[] { (byte) 0x16 }, (char) 928),
            new CharSet(new byte[] { (byte) 0x17 }, (char) 936),
            new CharSet(new byte[] { (byte) 0x18 }, (char) 931),
            new CharSet(new byte[] { (byte) 0x19 }, (char) 920),
            new CharSet(new byte[] { (byte) 0x40 }, (char) 161),
            new CharSet(new byte[] { (byte) 0x5b }, (char) 196),
            new CharSet(new byte[] { (byte) 0x5d }, (char) 209),
            new CharSet(new byte[] { (byte) 0x5f }, (char) 167),
            new CharSet(new byte[] { (byte) 0x60 }, (char) 191),
            new CharSet(new byte[] { (byte) 0x7b }, (char) 228),
            new CharSet(new byte[] { (byte) 0x7c }, (char) 246),
            new CharSet(new byte[] { (byte) 0x7d }, (char) 241),
            new CharSet(new byte[] { (byte) 0x7f }, (char) 224),
            new CharSet(new byte[] { (byte) 0x1a }, (char) 926),
            new CharSet(new byte[] { (byte) 0x1c }, (char) 198),
            new CharSet(new byte[] { (byte) 0x1d }, (char) 230),
            new CharSet(new byte[] { (byte) 0x1e }, (char) 223),
            new CharSet(new byte[] { (byte) 0x1f }, (char) 201),
            new CharSet(new byte[] { (byte) 0x24 }, (char) 164),
            new CharSet(new byte[] { (byte) 0x0e }, (char) 197),
            new CharSet(new byte[] { (byte) 0x0f }, (char) 229),
            new CharSet(new byte[] { (byte) 0x0c }, (char) 248),
            new CharSet(new byte[] { (byte) 0x0b }, (char) 216),
            new CharSet(new byte[] { (byte) 0x1b, 0x65 }, (char) 0x20AC), };

    private boolean          encodeWithEscape;
    private CharSet[]        myCharSet;

    public CharacterEncoderGSM() {
        this.setEncodeWithEscape(true);
        this.setMyCharSet(CharacterEncoderGSM.turkishToGSMCharSet);
    }

    public CharacterEncoderGSM(final boolean useEscapeChars) {
        this.setEncodeWithEscape(useEscapeChars);
        this.setMyCharSet(CharacterEncoderGSM.turkishToGSMCharSet);
    }

    public CharacterEncoderGSM(final boolean encodeWithEscape,
                               final CharSet[] myCharSet) {
        this.encodeWithEscape = encodeWithEscape;
        this.myCharSet = myCharSet;

    }

    @Override
    public byte[] encode(final String str) {
        if (str.length() < 1) {
            return new byte[0];
        }
        final ByteBuffer bytes = ByteBuffer.allocate((str.length() * 2) + 10);
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);
            if (c < 128) { // below comparisons are made from ASCII table (as we receive Java UTF16 characters from app)
                final short mostSignificant = (short) (c & (short) 0x00F0);
                final byte wholeByte = (byte) (c & (short) 0x00FF);
                switch (mostSignificant) {
                    case 0x0000:
                        if ((wholeByte == 0x0a) || (wholeByte == 0x0d)) { // LF, CR characters
                            bytes.put((byte) c);
                        } else if ((wholeByte == 0x0c) && this.encodeWithEscape) {
                            bytes.put((byte) 0x1b);
                            bytes.put((byte) 0x0a);
                        } else {
                            bytes.put((byte) 0x20);
                        }
                        break;
                    case 0x0010:
                        if (wholeByte == 0x1b) { // ESC character
                            bytes.put((byte) c);
                        } else {
                            bytes.put((byte) 0x20);
                        }
                        break;
                    case 0x0020:
                        if (wholeByte == 0x24) { // # character
                            bytes.put((byte) 0x02);
                        } else {
                            bytes.put((byte) c);
                        }
                        break;
                    case 0x0030:
                        bytes.put((byte) c);
                        break;
                    case 0x0040:
                        if (wholeByte == 0x40) { // @ character
                            bytes.put((byte) 0x00);
                        } else {
                            bytes.put((byte) c);
                        }
                        break;
                    case 0x0050:
                        if (wholeByte == 0x5b) { // [ character
                            if (this.encodeWithEscape) {
                                bytes.put((byte) 0x1b);
                                bytes.put((byte) 0x3c);
                            } else {
                                bytes.put((byte) 0x3c);
                            }

                        } else if (wholeByte == 0x5c) { // \ character
                            if (this.encodeWithEscape) {
                                bytes.put((byte) 0x1b);
                                bytes.put((byte) 0x2f);
                            } else {
                                bytes.put((byte) 0x2f);
                            }
                        } else if (wholeByte == 0x5d) { // ] character
                            if (this.encodeWithEscape) {
                                bytes.put((byte) 0x1b);
                                bytes.put((byte) 0x3e);
                            } else {
                                bytes.put((byte) 0x3e);
                            }
                        } else if (wholeByte == 0x5e) { // ^ character
                            if (this.encodeWithEscape) {
                                bytes.put((byte) 0x1b);
                                bytes.put((byte) 0x14);
                            } else {
                                bytes.put((byte) 0x27);
                            }
                        } else if (wholeByte == 0x5f) { // _ character
                            bytes.put((byte) 0x11);
                        } else {
                            bytes.put((byte) c);
                        }
                        break;
                    case 0x0060:
                        if (wholeByte == 0x60) { // ' character
                            bytes.put((byte) 0x27);
                        } else {
                            bytes.put((byte) c);
                        }
                        break;
                    case 0x0070:
                        if (wholeByte == 0x7b) { // { character
                            if (this.encodeWithEscape) {
                                bytes.put((byte) 0x1b);
                                bytes.put((byte) 0x28);
                            } else {
                                bytes.put((byte) 0x28);
                            }

                        } else if (wholeByte == 0x7c) { // | character
                            if (this.encodeWithEscape) {
                                bytes.put((byte) 0x1b);
                                bytes.put((byte) 0x40);
                            } else {
                                bytes.put((byte) 0x21);
                            }
                        } else if (wholeByte == 0x7d) { // } character
                            if (this.encodeWithEscape) {
                                bytes.put((byte) 0x1b);
                                bytes.put((byte) 0x29);
                            } else {
                                bytes.put((byte) 0x29);
                            }
                        } else if (wholeByte == 0x7e) { // ~ character
                            if (this.encodeWithEscape) {
                                bytes.put((byte) 0x1b);
                                bytes.put((byte) 0x3d);
                            } else {
                                bytes.put((byte) 0x22);
                            }
                        } else if (wholeByte == 0x7f) { // DEL character
                            bytes.put((byte) 0x20);
                        } else {
                            bytes.put((byte) c);
                        }
                        break;
                }
            } else {
                final byte[] trBytes = this.applyFilter(c, this.myCharSet);
                bytes.put(trBytes);
            }
        }
        final byte[] mBytes = new byte[bytes.position()];
        bytes.flip();
        bytes.get(mBytes);
        return mBytes;
    }

    public boolean isEncodeWithEscape() {
        return this.encodeWithEscape;
    }

    public void setEncodeWithEscape(final boolean encodeWithEscape) {
        this.encodeWithEscape = encodeWithEscape;
    }

    public CharSet[] getMyCharSet() {
        return this.myCharSet;
    }

    public void setMyCharSet(final CharSet[] myCharSet) {
        this.myCharSet = myCharSet;
    }

}
