package com.adenon.api.smpp.message;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.common.Smpp34Constants;
import com.adenon.api.smpp.logging.LoggerWrapper;
import com.adenon.api.smpp.sdk.AddressDescriptor;
import com.adenon.api.smpp.sdk.ENumberPlanIndicator;
import com.adenon.api.smpp.sdk.ETypeOfNumber;


public class Smpp34QuerySM implements MessageObject {

    final LoggerWrapper       logger;
    private String            paramMessageId;

    private AddressDescriptor sourceAddress;
    private AddressDescriptor destinationAddress;


    public Smpp34QuerySM(final LoggerWrapper _logger) {
        this.logger = _logger;
    }

    @Override
    public int getMesssageType() {
        return Smpp34Constants.MSG_QUERY_SM;
    }

    @Override
    public String getDescription() {
        return "QUERY_SM";
    }

    public void parseMessage(final ByteBuffer byteBuffer) throws Exception {
        final byte[] temp = new byte[256];
        int charCount = CommonUtils.getCOctetString(temp, byteBuffer);

        this.setSourceAddress(new AddressDescriptor());
        this.getSourceAddress().setTon(ETypeOfNumber.getTon(byteBuffer.get()));
        this.getSourceAddress().setNpi(ENumberPlanIndicator.getNpi(byteBuffer.get()));
        charCount = CommonUtils.getCOctetString(temp, byteBuffer);
        this.getSourceAddress().setNumber(new String(temp, 0, charCount));

    }

    public void fillBody(final ByteBuffer byteBuffer,
                         final int sequence_no) throws Exception {
        // header
        byteBuffer.position(4);
        byteBuffer.putInt(Smpp34Constants.MSG_QUERY_SM);
        byteBuffer.putInt(0); // command status
        byteBuffer.putInt(sequence_no);

        // mandatory parameters
        byte[] messageId = null;
        if (this.getParamMessageId() == null) {
            byteBuffer.put((byte) 0);
        } else {
            messageId = this.getParamMessageId().getBytes("ISO8859-1");
            byteBuffer.put(messageId);
            byteBuffer.put((byte) 0);
        }

        if (this.sourceAddress == null) {
            byteBuffer.put((byte) 0);
        } else {
            byteBuffer.put((byte) this.sourceAddress.getTon().getValue());
            byteBuffer.put((byte) this.sourceAddress.getNpi().getValue());
            byteBuffer.put(this.sourceAddress.getNumber().getBytes("ISO8859-1"));
            byteBuffer.put((byte) 0);
        }
        CommonUtils.setLength(byteBuffer);
    }

    public AddressDescriptor getSourceAddress() {
        return this.sourceAddress;
    }

    public void setSourceAddress(final AddressDescriptor sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public AddressDescriptor getDestinationAddress() {
        return this.destinationAddress;
    }

    public void setDestinationAddress(final AddressDescriptor destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getParamMessageId() {
        return this.paramMessageId;
    }

    public void setParamMessageId(final String paramMessageId) {
        this.paramMessageId = paramMessageId;
    }
}
