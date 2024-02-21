package com.adenon.api.smpp.message;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.common.CommonParameters;
import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.logging.LoggerWrapper;
import com.adenon.api.smpp.sdk.AddressDescriptor;
import com.adenon.api.smpp.sdk.ENumberPlanIndicator;
import com.adenon.api.smpp.sdk.ETypeOfNumber;
import com.adenon.library.common.utils.StringUtils;


public class MandatoryParameters {

    private final LoggerWrapper logger;

    private int                 paramESMClass;
    private String              paramServiceType;
    private AddressDescriptor   sourceAddress;
    private AddressDescriptor   destinationAddress;
    private int                 paramProtocolID = 0;
    private int                 paramPriorityFlag;
    private int                 paramRegisteredDelivery;
    private int                 paramReplaceIfPresentFlag;
    private int                 paramDataCoding = CommonParameters.DATA_CODING_SMSC_DEFAULT;
    private int                 paramSMDefaultMsgID;
    private int                 paramSMLength;
    private String              paramScheduleDeliveryTime;
    private String              paramValidityPeriod;
    private String              paramShortMessage;
    private byte[]              paramShortMessageBinary;
    private long                relativeValidityPeriod;
    private final int           messageType;

    public MandatoryParameters(final LoggerWrapper logger,
                               final int messageType) {
        this.logger = logger;
        this.messageType = messageType;
    }

    public void parseMandatoryParameters(final ByteBuffer byteBuffer,
                                         final long transID,
                                         final String label) throws Exception {
        this.setParamServiceType(CommonUtils.getCOctetStringEx(byteBuffer, 10));

        this.setSourceAddress(new AddressDescriptor());
        this.getSourceAddress().setTon(ETypeOfNumber.getTon(byteBuffer.get()));
        this.getSourceAddress().setNpi(ENumberPlanIndicator.getNpi(byteBuffer.get()));
        this.getSourceAddress().setNumber(CommonUtils.getCOctetStringEx(byteBuffer, 20));

        this.setDestinationAddress(new AddressDescriptor());
        this.getDestinationAddress().setTon(ETypeOfNumber.getTon(byteBuffer.get()));
        this.getDestinationAddress().setNpi(ENumberPlanIndicator.getNpi(byteBuffer.get()));
        this.getDestinationAddress().setNumber(CommonUtils.getCOctetStringEx(byteBuffer, 20));

        this.setParamESMClass(byteBuffer.get());
        this.setParamProtocolID(byteBuffer.get());
        this.setParamPriorityFlag(byteBuffer.get());
        this.setParamScheduleDeliveryTime(CommonUtils.getCOctetStringEx(byteBuffer, 25));
        this.setParamValidityPeriod(CommonUtils.getCOctetStringEx(byteBuffer, 25));
        this.setParamRegisteredDelivery(byteBuffer.get());
        this.setParamReplaceIfPresentFlag(byteBuffer.get());
        this.setParamDataCoding(byteBuffer.get());
        this.setParamSMDefaultMsgID(byteBuffer.get());
        this.setParamSMLength(0xff & byteBuffer.get());
        final byte encodingCode = (byte) ((byte) this.getParamDataCoding() & (byte) 0x0f);
        if (this.getParamSMLength() > 0) {
            try {
                switch (encodingCode) {
                    case CommonParameters.DATA_CODING_UCS2:
                        this.setParamShortMessage(CommonUtils.getOctetStringUnicodeEx(byteBuffer, this.getParamSMLength() / 2));
                        break;
                    case CommonParameters.DATA_CODING_ASCII:
                    case CommonParameters.DATA_CODING_ISO_8859_1:
                    case CommonParameters.DATA_CODING_SMSC_DEFAULT:
                        this.setParamShortMessage(CommonUtils.getOctetStringEx(byteBuffer, this.getParamSMLength()));
                        break;
                    case CommonParameters.DATA_CODING_BINARY:
                        this.setParamShortMessageBinary(new byte[this.getParamSMLength()]);
                        byteBuffer.get(this.getParamShortMessageBinary());
                        break;
                    default:
                        this.setParamShortMessage(CommonUtils.getOctetStringEx(byteBuffer, this.getParamSMLength()));
                        break;
                }
            } catch (final Exception e) {
                this.logger.error("MandatoryParameters", "parseMessage", transID, label, " : Error : " + e.getMessage(), e);
            }
        } else {
            this.setParamShortMessage("");
        }
    }

    public void fillHeader(final ByteBuffer byteBuffer,
                           final int sequence_no) throws Exception {
    	for (int i = 0; i < 1200; i++) {
    		String bytesToHexFormated = CommonUtils.bytesToHexFormated(byteBuffer);
    		if (bytesToHexFormated.contains("00AA00")){
    			System.out.println("hello there");
    		}
		}

        byteBuffer.position(4);
        byteBuffer.putInt(this.messageType);
        byteBuffer.putInt(0);
        byteBuffer.putInt(sequence_no);
    }

    public void fillMandatoryParameters(final ByteBuffer byteBuffer,
                                        final int sequence_no) throws Exception {
        this.fillHeader(byteBuffer, sequence_no);
        byte[] serviceType = null;
        if (this.getParamServiceType() == null) {
            byteBuffer.put((byte) 0);
        } else {
            serviceType = this.getParamServiceType().getBytes("ISO8859-1");
            byteBuffer.put(serviceType);
            byteBuffer.put((byte) 0);
        }

        if (this.getSourceAddress() == null) {
            byteBuffer.put((byte) 0);
            byteBuffer.put((byte) 0);
            byteBuffer.put((byte) 0);
        } else {
            byteBuffer.put((byte) this.getSourceAddress().getTon().getValue());
            byteBuffer.put((byte) this.getSourceAddress().getNpi().getValue());
            if (this.getSourceAddress().getNumber() != null) {
                byteBuffer.put(this.getSourceAddress().getNumber().getBytes("ISO8859-1"));
            }
            byteBuffer.put((byte) 0);
        }
        if (this.getDestinationAddress() == null) {
            byteBuffer.put((byte) 0);
            byteBuffer.put((byte) 0);
            byteBuffer.put((byte) 0);
        } else {
            byteBuffer.put((byte) this.getDestinationAddress().getTon().getValue());
            byteBuffer.put((byte) this.getDestinationAddress().getNpi().getValue());
            if (this.getDestinationAddress().getNumber() != null) {
                byteBuffer.put(this.getDestinationAddress().getNumber().getBytes("ISO8859-1"));
            }
            byteBuffer.put((byte) 0);
        }
        if (this.getParamESMClass() < 0) {
            this.setParamESMClass(0);
        }
        byteBuffer.put((byte) this.getParamESMClass());
        byteBuffer.put((byte) this.getParamProtocolID());
        byteBuffer.put((byte) this.getParamPriorityFlag());
        if (this.getParamScheduleDeliveryTime() == null) {
            byteBuffer.put((byte) 0);
        } else {
            byte[] scheduleDel = null;
            scheduleDel = this.getParamScheduleDeliveryTime().getBytes("ISO8859-1");
            if (scheduleDel.length != 16) {
                throw new ProtocolException("schedule_delivery_time parameter length must be 16. But the parameter length is " + scheduleDel.length);
            }
            byteBuffer.put(scheduleDel);
            byteBuffer.put((byte) 0);
        }
        if (this.getParamValidityPeriod() == null) {
            byteBuffer.put((byte) 0);
        } else {
            byte[] validityPer = null;
            validityPer = this.getParamValidityPeriod().getBytes("ISO8859-1");
            if (validityPer.length != 16) {
                throw new ProtocolException("validity_period parameter length must be 16. But the parameter length is " + validityPer);
            }
            byteBuffer.put(validityPer);
            byteBuffer.put((byte) 0);
        }
        byteBuffer.put((byte) this.getParamRegisteredDelivery());
        byteBuffer.put((byte) this.getParamReplaceIfPresentFlag());
        byteBuffer.put((byte) this.getParamDataCoding());
        byteBuffer.put((byte) this.getParamSMDefaultMsgID());
    }

    public int getParamESMClass() {
        return this.paramESMClass;
    }

    public void setParamESMClass(final int paramESMClass) {
        this.paramESMClass = paramESMClass;
    }

    public String getParamServiceType() {
        return this.paramServiceType;
    }

    public void setParamServiceType(final String paramServiceType) {
        this.paramServiceType = paramServiceType;
    }

    public int getParamProtocolID() {
        return this.paramProtocolID;
    }

    public void setParamProtocolID(final int paramProtocolID) {
        this.paramProtocolID = paramProtocolID;
    }

    public int getParamPriorityFlag() {
        return this.paramPriorityFlag;
    }

    public void setParamPriorityFlag(final int paramPriorityFlag) {
        this.paramPriorityFlag = paramPriorityFlag;
    }

    public String getParamScheduleDeliveryTime() {
        return this.paramScheduleDeliveryTime;
    }

    public void setParamScheduleDeliveryTime(final String paramScheduleDeliveryTime) {
        this.paramScheduleDeliveryTime = paramScheduleDeliveryTime;
    }

    public String getParamValidityPeriod() {
        return this.paramValidityPeriod;
    }

    public void setParamValidityPeriod(final String paramValidityPeriod) {
        this.paramValidityPeriod = paramValidityPeriod;
        if (!StringUtils.checkStringIsEmpty(paramValidityPeriod)) {
            if (paramValidityPeriod.endsWith("R")) {
                this.relativeValidityPeriod = CommonUtils.milisecondsFromRelativeString(paramValidityPeriod);
            }

        }

    }

    public int getParamRegisteredDelivery() {
        return this.paramRegisteredDelivery;
    }

    public void setParamRegisteredDelivery(final int paramRegisteredDelivery) {
        this.paramRegisteredDelivery = paramRegisteredDelivery;
    }

    public int getParamReplaceIfPresentFlag() {
        return this.paramReplaceIfPresentFlag;
    }

    public void setParamReplaceIfPresentFlag(final int paramReplaceIfPresentFlag) {
        this.paramReplaceIfPresentFlag = paramReplaceIfPresentFlag;
    }

    public int getParamDataCoding() {
        return this.paramDataCoding;
    }

    public void setParamDataCoding(final int paramDataCoding) {
        this.paramDataCoding = paramDataCoding;
    }

    public int getParamSMDefaultMsgID() {
        return this.paramSMDefaultMsgID;
    }

    public void setParamSMDefaultMsgID(final int paramSMDefaultMsgID) {
        this.paramSMDefaultMsgID = paramSMDefaultMsgID;
    }

    public int getParamSMLength() {
        return this.paramSMLength;
    }

    public void setParamSMLength(final int paramSMLength) {
        this.paramSMLength = paramSMLength;
    }

    public String getParamShortMessage() {
        return this.paramShortMessage;
    }

    public void setParamShortMessage(final String paramShortMessage) {
        this.paramShortMessage = paramShortMessage;
    }

    public byte[] getParamShortMessageBinary() {
        return this.paramShortMessageBinary;
    }

    public void setParamShortMessageBinary(final byte[] paramShortMessageBinary) {
        this.paramShortMessageBinary = paramShortMessageBinary;
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

    @Override
    public String toString() {
        return " source : " + this.getSourceAddress() + " destination : " + this.getDestinationAddress() + " message : " + this.getParamShortMessage();
    }

    public long getRelativeValidityPeriod() {
        return this.relativeValidityPeriod;
    }

    public void setRelativeValidityPeriod(final long relativeValidityPeriod) {
        this.relativeValidityPeriod = relativeValidityPeriod;
    }

}