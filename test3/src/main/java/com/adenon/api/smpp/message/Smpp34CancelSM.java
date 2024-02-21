package com.adenon.api.smpp.message;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.common.Smpp34Constants;
import com.adenon.api.smpp.logging.LoggerWrapper;
import com.adenon.api.smpp.sdk.AddressDescriptor;
import com.adenon.api.smpp.sdk.ENumberPlanIndicator;
import com.adenon.api.smpp.sdk.ETypeOfNumber;


public class Smpp34CancelSM implements MessageObject {

    private final LoggerWrapper logger;
    private String              paramServiceType;
    private String              paramMessageId;

    private AddressDescriptor   sourceAddress;
    private AddressDescriptor   destinationAddress;


    public Smpp34CancelSM(final LoggerWrapper _logger) {
        this.logger = _logger;
    }

    public void parseMessage(final ByteBuffer byteBuffer) throws Exception {
        final byte[] temp = new byte[256];
        int charCount = CommonUtils.getCOctetString(temp, byteBuffer);
        this.setParamServiceType(new String(temp, 0, charCount));

        this.setSourceAddress(new AddressDescriptor());
        this.getSourceAddress().setTon(ETypeOfNumber.getTon(byteBuffer.get()));
        this.getSourceAddress().setNpi(ENumberPlanIndicator.getNpi(byteBuffer.get()));
        charCount = CommonUtils.getCOctetString(temp, byteBuffer);
        this.getSourceAddress().setNumber(new String(temp, 0, charCount));

        this.setDestinationAddress(new AddressDescriptor());
        this.getDestinationAddress().setTon(ETypeOfNumber.getTon(byteBuffer.get()));
        this.getDestinationAddress().setNpi(ENumberPlanIndicator.getNpi(byteBuffer.get()));
        charCount = CommonUtils.getCOctetString(temp, byteBuffer);
        this.getDestinationAddress().setNumber(new String(temp, 0, charCount));

    }

    public void fillBody(final ByteBuffer byteBuffer,
                         final int sequence_no) throws Exception {
        byteBuffer.position(4);
        byteBuffer.putInt(Smpp34Constants.MSG_CANCEL_SM);
        byteBuffer.putInt(0);
        byteBuffer.putInt(sequence_no);
        byte[] serviceType = null;
        if (this.getParamServiceType() == null) {
            byteBuffer.put((byte) 0);
        } else {
            serviceType = this.getParamServiceType().getBytes("ISO8859-1");
            byteBuffer.put(serviceType);
            byteBuffer.put((byte) 0);
        }
        if (this.sourceAddress == null) {
            this.sourceAddress = new AddressDescriptor();
        }
        byteBuffer.put((byte) this.sourceAddress.getTon().getValue());
        byteBuffer.put((byte) this.sourceAddress.getNpi().getValue());
        if (this.sourceAddress.getNumber() == null) {
            byteBuffer.put((byte) 0);
        } else {
            byteBuffer.put(this.sourceAddress.getNumber().getBytes("ISO8859-1"));
            byteBuffer.put((byte) 0);
        }

        if (this.destinationAddress == null) {
            this.destinationAddress = new AddressDescriptor();
        }
        byteBuffer.put((byte) this.destinationAddress.getTon().getValue());
        byteBuffer.put((byte) this.destinationAddress.getNpi().getValue());
        if (this.destinationAddress.getNumber() == null) {
            byteBuffer.put((byte) 0);
        } else {
            byteBuffer.put(this.destinationAddress.getNumber().getBytes("ISO8859-1"));
            byteBuffer.put((byte) 0);
        }

    }


    @Override
    public int getMesssageType() {
        return Smpp34Constants.MSG_SUBMIT_SM;
    }

    @Override
    public String getDescription() {
        return "CANCEL_SM";
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

    public String getParamServiceType() {
        return this.paramServiceType;
    }

    public void setParamServiceType(final String paramServiceType) {
        this.paramServiceType = paramServiceType;
    }

}
