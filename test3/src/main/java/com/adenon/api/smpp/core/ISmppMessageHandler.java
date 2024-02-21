package com.adenon.api.smpp.core;

import java.nio.ByteBuffer;


public interface ISmppMessageHandler {

    void handleMsg(ByteBuffer byteBuffer) throws Exception;

}