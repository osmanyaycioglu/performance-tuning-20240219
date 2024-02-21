package com.adenon.api.smpp.common.encoder;


public interface ICharacterEncoder {

    public byte[] encode(String str);
}
