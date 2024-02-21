package com.adenon.api.smpp.messaging.processor;

import java.nio.ByteBuffer;


public interface ICharacterProcessor {

    void process(String str);

    void fillMessageBody(ByteBuffer buffer,
                         int index,
                         byte[] concatHeader) throws Exception;

    int getPartCount();

}