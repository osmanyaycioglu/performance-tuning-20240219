package com.adenon.api.smpp.wappush;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.sdk.EMessageType;


public interface IWapMessage {

    public void encode(ByteBuffer byteBuffer) throws Exception;

    public EMessageType getMessageType();

    public void encodeUDHBytes(ByteBuffer byteBuffer);

    public void encodeUDHBytes(ByteBuffer byteBuffer,
                               int msgCount,
                               int msgIndex,
                               int msgId);

    public void encodeWSPBytes(ByteBuffer buffer) throws Exception;

    public int getWSPBytesLength();


}
