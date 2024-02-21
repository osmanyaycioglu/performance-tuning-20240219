package com.adenon.api.smpp.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.adenon.api.smpp.common.CommonParameters;
import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.common.Smpp34Constants;
import com.adenon.api.smpp.logging.LoggerWrapper;
import com.adenon.api.smpp.sdk.AddressDescriptor;
import com.adenon.api.smpp.sdk.ENumberPlanIndicator;
import com.adenon.api.smpp.sdk.ETypeOfNumber;

public class SubmitMultiSMMessage extends SubmitSMMessage {

    private List<AddressDescriptor> destinationAddresses;

    public SubmitMultiSMMessage(final LoggerWrapper logger,
                                final int transactionId,
                                final String label) {
        super(logger, transactionId, label);
    }

    @Override
    public void fillMandatoryParameters(final ByteBuffer byteBuffer,
                                        final int sequence_no) throws Exception {
        byteBuffer.position(4); // length will be set at the end.
        byteBuffer.putInt(Smpp34Constants.MSG_SUBMIT_MULTI_SM);
        byteBuffer.putInt(0); // command status
        byteBuffer.putInt(sequence_no); // sequence number (transaction id)

        // Service type
        byte[] serviceType = null;
        if (this.getParamServiceType() == null) {
            byteBuffer.put((byte) 0);
        } else {
            serviceType = this.getParamServiceType().getBytes("ISO8859-1");
            byteBuffer.put(serviceType);
            byteBuffer.put((byte) 0);
        }

        // Source address (ton, npi, number)
        if (this.getSourceAddress() == null) {
            byteBuffer.put((byte) 0);
            byteBuffer.put((byte) 0);
            byteBuffer.put((byte) 0);
        } else {
            byteBuffer.put((byte) this.getSourceAddress().getTon().getValue());
            byteBuffer.put((byte) this.getSourceAddress().getNpi().getValue());
            byteBuffer.put(this.getSourceAddress().getNumber().getBytes("ISO8859-1"));
            byteBuffer.put((byte) 0);
        }

        byteBuffer.put((byte) this.destinationAddresses.size()); // number_of_dests
        // dest_address(es) (dest_flag, ton, npi, number)
        for (int i = 0; i < this.destinationAddresses.size(); i++) {
            final AddressDescriptor addressDescriptor = this.getDestinationAddresses().get(i);
            if (addressDescriptor == null) {
                byteBuffer.put((byte) 0);
                byteBuffer.put((byte) 0);
                byteBuffer.put((byte) 0);
            } else {
                byteBuffer.put((byte) 1); // dest_flag (1 for SME address)
                byteBuffer.put((byte) addressDescriptor.getTon().getValue());
                byteBuffer.put((byte) addressDescriptor.getNpi().getValue());
                byteBuffer.put(addressDescriptor.getNumber().getBytes("ISO8859-1"));
                byteBuffer.put((byte) 0);
            }
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

    @Override
    public void fillOptionalParameters(final ByteBuffer byteBuffer) throws Exception {
        if (this.getOpParamUserMessageReference() >= 0) {
            CommonUtils.putShortToOptBuffer(Smpp34Constants.OPT_USER_MESSAGE_REFERENCE, (short) this.getOpParamUserMessageReference(), byteBuffer);
        }
        if ((this.getOpParamSarMsgRefNum() >= 0)) {
            CommonUtils.putShortToOptBuffer(Smpp34Constants.OPT_SAR_MSG_REF_NUM, (short) this.getOpParamSarMsgRefNum(), byteBuffer);
        }
        if ((this.getOpParamSarTotalSegments() >= 0)) {
            CommonUtils.putByteToOptBuffer(Smpp34Constants.OPT_SAR_TOTAL_SEGMENTS, (byte) this.getOpParamSarTotalSegments(), byteBuffer);
        }
        if ((this.getOpParamSarSegmentSequenceNum() >= 0)) {
            CommonUtils.putByteToOptBuffer(Smpp34Constants.OPT_SAR_SEGMENT_SEQNUM, (byte) this.getOpParamSarSegmentSequenceNum(), byteBuffer);
        }
    }

    public List<AddressDescriptor> getDestinationAddresses() {
        return this.destinationAddresses;
    }

    @Override
    public void parseMandatoryParameters(final ByteBuffer byteBuffer,
                                         final long transID,
                                         final String label) throws Exception {

        this.setParamServiceType(CommonUtils.getCOctetStringEx(byteBuffer, 10));

        this.setSourceAddress(new AddressDescriptor());
        this.getSourceAddress().setTon(ETypeOfNumber.getTon(byteBuffer.get()));
        this.getSourceAddress().setNpi(ENumberPlanIndicator.getNpi(byteBuffer.get()));
        this.getSourceAddress().setNumber(CommonUtils.getCOctetStringEx(byteBuffer, 20));

        final int destNumber = byteBuffer.get(); // number_of_dests
        final List<AddressDescriptor> destAddresses = new ArrayList<AddressDescriptor>(destNumber);
        for (int i = 0; i < destNumber; i++) {
            final AddressDescriptor address = new AddressDescriptor();
            byteBuffer.get(); // dest_flag

            address.setTon(ETypeOfNumber.getTon(byteBuffer.get()));
            address.setNpi(ENumberPlanIndicator.getNpi(byteBuffer.get()));
            address.setNumber(CommonUtils.getCOctetStringEx(byteBuffer, 20));
            destAddresses.add(address);
        }
        this.setDestinationAddresses(destAddresses);

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
                    default:
                        this.setParamShortMessage(CommonUtils.getOctetStringEx(byteBuffer, this.getParamSMLength()));
                        break;
                }
            } catch (final Exception e) {
                this.getLogger().error("MandatoryParameters", "parseMessage", transID, label, " : Error : " + e.getMessage(), e);
            }
        } else {
            this.setParamShortMessage("");
        }

    }

    @Override
    public void parseMessage(final ByteBuffer byteBuffer) throws Exception {
        this.parseMandatoryParameters(byteBuffer, this.getTransID(), this.getLabel());
        this.parseOpitionalParameters(byteBuffer, this.getTransID(), this.getLabel());
    }

    @Override
    public void parseOpitionalParameters(final ByteBuffer byteBuffer,
                                         final long transID,
                                         final String label) {
        try {
            while (byteBuffer.limit() > byteBuffer.position()) {
                final int parameterTag = byteBuffer.getShort();
                final int parameterLength = byteBuffer.getShort();
                switch (parameterTag) {
                    case Smpp34Constants.OPT_RECEIPTED_MESSAGE_ID:
                        this.setOpParamMessageId(CommonUtils.getCOctetStringEx(byteBuffer, 32));
                        break;
                    case Smpp34Constants.OPT_USER_MESSAGE_REFERENCE:
                        this.setOpParamUserMessageReference(byteBuffer.getShort());
                        break;
                    case Smpp34Constants.OPT_SAR_MSG_REF_NUM:
                        this.setOpParamSarMsgRefNum(CommonUtils.getIntegerWithLen(parameterLength, byteBuffer));
                        break;
                    case Smpp34Constants.OPT_SAR_TOTAL_SEGMENTS:
                        this.setOpParamSarTotalSegments(byteBuffer.get());
                        break;
                    case Smpp34Constants.OPT_SAR_SEGMENT_SEQNUM:
                        this.setOpParamSarSegmentSequenceNum(byteBuffer.get());
                        break;
                    case Smpp34Constants.OPT_MESSAGE_PAYLOAD:
                        this.setOpParamPayload(CommonUtils.getOctetStringEx(byteBuffer, parameterLength));
                    case Smpp34Constants.OPT_MSG_STATE:
                        this.setOpParamMessageState(byteBuffer.get());
                        break;
                    default:
                        this.getLogger().warn("OptionalParameters",
                                              "parseOpitionalParameters",
                                              transID,
                                              label,
                                              " Unsupported optional parameter id : " + parameterTag);
                        final byte[] optData = new byte[parameterLength];
                        byteBuffer.get(optData);
                        final OptionalParameter optParam = new OptionalParameter();
                        optParam.setOptionalParameterData(optData);
                        optParam.setOptionalParameterTag(parameterTag);
                        if (this.getOptParamsList() == null) {
                            this.setOptParamsList(new ArrayList<OptionalParameter>());
                        }
                        this.getOptParamsList().add(optParam);
                        break;
                }
            }
        } catch (final Exception e) {
            this.getLogger().error("SubmitSMMessage", "getOpParams", transID, label, " : Error : " + e.getMessage(), e);
        }

        if (this.getOpParamSarTotalSegments() < -1) {
            this.setOpParamSarTotalSegments(1);
        }
        if (this.getOpParamSarSegmentSequenceNum() < -1) {
            this.setOpParamSarSegmentSequenceNum(1);
        }
    }

    public void setDestinationAddresses(final List<AddressDescriptor> destinationAddresses) {
        this.destinationAddresses = destinationAddresses;
    }
}
