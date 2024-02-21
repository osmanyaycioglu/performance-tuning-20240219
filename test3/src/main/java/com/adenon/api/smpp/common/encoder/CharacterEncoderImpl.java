package com.adenon.api.smpp.common.encoder;


public abstract class CharacterEncoderImpl implements ICharacterEncoder {

    protected byte[] applyFilter(final char c,
                                 final CharSet[] myCharSet) {
        for (int i = 0; i < myCharSet.length; i++) {
            if (myCharSet[i].getOldChar() == c) {
                return myCharSet[i].getEqualBytes();
            }
        }
        return new byte[] { 32 };
    }


}
