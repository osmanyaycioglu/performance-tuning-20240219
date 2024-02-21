package com.adenon.api.smpp.common.encoder;

public class CharacterEncoderSingleShiftTurkish extends CharacterEncoderImpl {

    public static void main(final String[] args) {
        for (int i = 0; i < CharacterEncoderSingleShiftTurkish.turkishToSingleShift.length; i++) {
            System.out.println(CharacterEncoderSingleShiftTurkish.turkishToSingleShift[i].getOldChar());
        }
    }

    private static CharSet[]          turkishToSingleShift = new CharSet[] { new CharSet(new byte[] { (byte) 0x1b, 0x67 }, (char) 0x11f), // ğ *
            new CharSet(new byte[] { (byte) 0x1b, 0x47 }, (char) 0x11e), // Ğ *
            new CharSet(new byte[] { (byte) 0x1b, 0x73 }, (char) 0x15f), // ş *
            new CharSet(new byte[] { (byte) 0x1b, 0x53 }, (char) 0x15e), // Ş *
            new CharSet(new byte[] { (byte) 0x1b, 0x69 }, (char) 0x131), // ı *
            new CharSet(new byte[] { (byte) 0x1b, 0x49 }, (char) 0x130), // İ *
            new CharSet(new byte[] { (byte) 0x1b, 0x63 }, (char) 0x00e7), // ç *
            new CharSet(new byte[] { (byte) 0x7c }, (char) 0x00F6), // ö
            new CharSet(new byte[] { (byte) 0x5c }, (char) 0x00D6), // Ö
            new CharSet(new byte[] { (byte) 0x7e }, (char) 0x00FC), // ü
            new CharSet(new byte[] { (byte) 0x5e }, (char) 0x00DC), // Ü
            new CharSet(new byte[] { (byte) 0x09 }, (char) 0x00C7), // Ç
            new CharSet(new byte[] { (byte) 0x01 }, (char) 0x00A3), // £
            new CharSet(new byte[] { (byte) 0x03 }, (char) 165), // ¥
            new CharSet(new byte[] { (byte) 0x04 }, (char) 232), // è
            new CharSet(new byte[] { (byte) 0x05 }, (char) 233), // é
            new CharSet(new byte[] { (byte) 0x06 }, (char) 249), // ù
            new CharSet(new byte[] { (byte) 0x07 }, (char) 236), // ì
            new CharSet(new byte[] { (byte) 0x08 }, (char) 242), // ò
            new CharSet(new byte[] { (byte) 0x10 }, (char) 916), // Δ
            new CharSet(new byte[] { (byte) 0x12 }, (char) 934), // Φ
            new CharSet(new byte[] { (byte) 0x13 }, (char) 915), // Γ
            new CharSet(new byte[] { (byte) 0x14 }, (char) 923), // Λ
            new CharSet(new byte[] { (byte) 0x15 }, (char) 937), // Ω
            new CharSet(new byte[] { (byte) 0x16 }, (char) 928), // Π
            new CharSet(new byte[] { (byte) 0x17 }, (char) 936), // Ψ
            new CharSet(new byte[] { (byte) 0x18 }, (char) 931), // Σ
            new CharSet(new byte[] { (byte) 0x19 }, (char) 920), // Θ
            new CharSet(new byte[] { (byte) 0x40 }, (char) 161), // ¡
            new CharSet(new byte[] { (byte) 0x5b }, (char) 196), // Ä
            new CharSet(new byte[] { (byte) 0x5d }, (char) 209), // Ñ
            new CharSet(new byte[] { (byte) 0x5f }, (char) 167), // §
            new CharSet(new byte[] { (byte) 0x60 }, (char) 191), // ¿
            new CharSet(new byte[] { (byte) 0x7b }, (char) 228), // ä
            new CharSet(new byte[] { (byte) 0x7c }, (char) 246), // ö
            new CharSet(new byte[] { (byte) 0x7d }, (char) 241), // ñ
            new CharSet(new byte[] { (byte) 0x7f }, (char) 224), // à
            new CharSet(new byte[] { (byte) 0x1a }, (char) 926), // Ξ
            new CharSet(new byte[] { (byte) 0x1c }, (char) 198), // Æ
            new CharSet(new byte[] { (byte) 0x1d }, (char) 230), // æ
            new CharSet(new byte[] { (byte) 0x1e }, (char) 223), // ß
            new CharSet(new byte[] { (byte) 0x1f }, (char) 201), // É
            new CharSet(new byte[] { (byte) 0x24 }, (char) 164), // ¤
            new CharSet(new byte[] { (byte) 0x0e }, (char) 197), // Å
            new CharSet(new byte[] { (byte) 0x0f }, (char) 229), // å
            new CharSet(new byte[] { (byte) 0x0c }, (char) 248), // ø
            new CharSet(new byte[] { (byte) 0x0b }, (char) 216), // Ø
            new CharSet(new byte[] { (byte) 0x1b, 0x65 }, (char) 0x20AC), }; // €

    private final CharacterEncoderGSM characterEncoderGSM;

    public CharacterEncoderSingleShiftTurkish() {
        this.characterEncoderGSM = new CharacterEncoderGSM(true, CharacterEncoderSingleShiftTurkish.turkishToSingleShift);
    }

    @Override
    public byte[] encode(final String str) {
        return this.characterEncoderGSM.encode(str);
    }

}
