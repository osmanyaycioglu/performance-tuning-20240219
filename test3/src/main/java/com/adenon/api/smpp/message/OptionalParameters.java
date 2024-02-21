package com.adenon.api.smpp.message;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.common.Smpp34Constants;
import com.adenon.api.smpp.logging.LoggerWrapper;
import com.adenon.library.common.utils.StringUtils;

public class OptionalParameters extends MandatoryParameters {

    private final LoggerWrapper     logger;

    private int                     opParamUserMessageReference  = -1;
    private int                     opParamSarMsgRefNum          = -1;
    private int                     opParamSarTotalSegments      = -1;
    private int                     opParamSarSegmentSequenceNum = -1;
    private int                     opParamUSSDServiceOp         = -1;
    private int                     opParamMessageState          = -1;
    private String                  opParamPayload;
    private String                  opParamMessageId;
    private List<OptionalParameter> optParamsList;
    private boolean                 putConcatOptionalParameters  = true;

    public OptionalParameters(final LoggerWrapper logger,
                              final int messageType) {
        super(logger,
              messageType);
        this.logger = logger;
    }

    public void parseOpitionalParameters(final ByteBuffer byteBuffer,
                                         final long transID,
                                         final String label) {
        try {
            while (byteBuffer.limit() > byteBuffer.position()) {
                final int parameterTag = byteBuffer.getShort();
                final int parameterLength = byteBuffer.getShort();
                switch (parameterTag) {
                    case Smpp34Constants.OPT_RECEIPTED_MESSAGE_ID:
                        this.setOpParamMessageId(CommonUtils.getCOctetStringEx(byteBuffer,
                                                                               32));
                        break;
                    case Smpp34Constants.OPT_USER_MESSAGE_REFERENCE:
                        this.setOpParamUserMessageReference(byteBuffer.getShort());
                        break;
                    case Smpp34Constants.OPT_SAR_MSG_REF_NUM:
                        this.setOpParamSarMsgRefNum(CommonUtils.getIntegerWithLen(parameterLength,
                                                                                  byteBuffer));
                        break;
                    case Smpp34Constants.OPT_SAR_TOTAL_SEGMENTS:
                        this.setOpParamSarTotalSegments(byteBuffer.get());
                        break;
                    case Smpp34Constants.OPT_SAR_SEGMENT_SEQNUM:
                        this.setOpParamSarSegmentSequenceNum(byteBuffer.get());
                        break;
                    case Smpp34Constants.OPT_USSD_SERVICE_OP:
                        this.setOpParamUSSDServiceOp(CommonUtils.getIntegerWithLen(parameterLength,
                                                                                   byteBuffer));
                        break;
                    case Smpp34Constants.OPT_MESSAGE_PAYLOAD:
                        this.setOpParamPayload(CommonUtils.getOctetStringEx(byteBuffer,
                                                                            parameterLength));
                    case Smpp34Constants.OPT_MSG_STATE:
                        this.setOpParamMessageState(byteBuffer.get());
                        break;
                    default:
                        this.logger.warn("OptionalParameters",
                                         "parseOpitionalParameters",
                                         transID,
                                         label,
                                         " Unsupported optional parameter id : "
                                                + parameterTag);
                        final byte[] optData = new byte[parameterLength];
                        byteBuffer.get(optData);
                        final OptionalParameter optParam = new OptionalParameter();
                        optParam.setOptionalParameterData(optData);
                        optParam.setOptionalParameterTag(parameterTag);
                        if (this.getOptParamsList() == null) {
                            this.setOptParamsList(new ArrayList<OptionalParameter>());
                        }
                        this.getOptParamsList()
                            .add(optParam);
                        break;
                }
            }
        } catch (final Exception e) {
            this.logger.error("SubmitSMMessage",
                              "getOpParams",
                              transID,
                              label,
                              " : Error : "
                                     + e.getMessage(),
                              e);
        }

        if (this.getOpParamSarTotalSegments() < -1) {
            this.setOpParamSarTotalSegments(1);
        }
        if (this.getOpParamSarSegmentSequenceNum() < -1) {
            this.setOpParamSarSegmentSequenceNum(1);
        }
        for (int i = 0; i < 200; i++) {
            StringUtils.generateUUID();
        }
    }

    public void fillOptionalParameters(final ByteBuffer byteBuffer) throws Exception {
        if (this.getOpParamUserMessageReference() >= 0) {
            CommonUtils.putShortToOptBuffer(Smpp34Constants.OPT_USER_MESSAGE_REFERENCE,
                                            (short) this.getOpParamUserMessageReference(),
                                            byteBuffer);
        }
        if ((this.getOpParamSarMsgRefNum() >= 0)
            && this.putConcatOptionalParameters) {
            CommonUtils.putShortToOptBuffer(Smpp34Constants.OPT_SAR_MSG_REF_NUM,
                                            (short) this.getOpParamSarMsgRefNum(),
                                            byteBuffer);
        }
        if ((this.getOpParamSarTotalSegments() >= 0)
            && this.putConcatOptionalParameters) {
            CommonUtils.putByteToOptBuffer(Smpp34Constants.OPT_SAR_TOTAL_SEGMENTS,
                                           (byte) this.getOpParamSarTotalSegments(),
                                           byteBuffer);
        }
        if ((this.getOpParamSarSegmentSequenceNum() >= 0)
            && this.putConcatOptionalParameters) {
            CommonUtils.putByteToOptBuffer(Smpp34Constants.OPT_SAR_SEGMENT_SEQNUM,
                                           (byte) this.getOpParamSarSegmentSequenceNum(),
                                           byteBuffer);
        }
        if (this.getOpParamUSSDServiceOp() >= 0) {
            CommonUtils.putByteToOptBuffer(Smpp34Constants.OPT_USSD_SERVICE_OP,
                                           (byte) this.getOpParamUSSDServiceOp(),
                                           byteBuffer);
        }
        if (!CommonUtils.checkStringIsEmpty(this.getOpParamMessageId())) {
            CommonUtils.putCStringBytesToOptBuffer(Smpp34Constants.OPT_RECEIPTED_MESSAGE_ID,
                                                   this.getOpParamMessageId()
                                                       .getBytes("iso8859-1"),
                                                   byteBuffer);
        }
        if (this.getOpParamMessageState() > -1) {
            CommonUtils.putByteToOptBuffer(Smpp34Constants.OPT_MSG_STATE,
                                           (byte) this.getOpParamMessageState(),
                                           byteBuffer);
        }
        if (this.optParamsList != null) {
            for (final OptionalParameter optionalParameter : this.optParamsList) {
                CommonUtils.putCStringBytesToOptBuffer(optionalParameter.getOptionalParameterTag(),
                                                       optionalParameter.getOptionalParameterData(),
                                                       byteBuffer);
            }
        }

    }


    public int getOpParamUserMessageReference() {
        return this.opParamUserMessageReference;
    }

    public void setOpParamUserMessageReference(final int opParamUserMessageReference) {
        this.opParamUserMessageReference = opParamUserMessageReference;
    }

    public int getOpParamSarMsgRefNum() {
        return this.opParamSarMsgRefNum;
    }

    public void setOpParamSarMsgRefNum(final int opParamSarMsgRefNum) {
        this.opParamSarMsgRefNum = opParamSarMsgRefNum;
    }

    public int getOpParamSarTotalSegments() {
        return this.opParamSarTotalSegments;
    }

    public void setOpParamSarTotalSegments(final int opParamSarTotalSegments) {
        this.opParamSarTotalSegments = opParamSarTotalSegments;
    }

    public int getOpParamSarSegmentSequenceNum() {
        return this.opParamSarSegmentSequenceNum;
    }

    public void setOpParamSarSegmentSequenceNum(final int opParamSarSegmentSequenceNum) {
        this.opParamSarSegmentSequenceNum = opParamSarSegmentSequenceNum;
    }

    public int getOpParamUSSDServiceOp() {
        return this.opParamUSSDServiceOp;
    }

    public void setOpParamUSSDServiceOp(final int opParamUSSDServiceOp) {
        this.opParamUSSDServiceOp = opParamUSSDServiceOp;
    }


    public List<OptionalParameter> getOptParamsList() {
        return this.optParamsList;
    }

    public void setOptParamsList(final List<OptionalParameter> optParamsList) {
        this.optParamsList = optParamsList;
    }

    public String getOpParamPayload() {
        return this.opParamPayload;
    }

    public void setOpParamPayload(final String opParamPayload) {
        this.opParamPayload = opParamPayload;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public int getOpParamMessageState() {
        return this.opParamMessageState;
    }

    public void setOpParamMessageState(final int opParamMessageState) {
        this.opParamMessageState = opParamMessageState;
    }

    public String getOpParamMessageId() {
        return this.opParamMessageId;
    }

    public void setOpParamMessageId(final String opParamMessageId) {
        this.opParamMessageId = opParamMessageId;
    }

    public boolean isPutConcatOptionalParameters() {
        return this.putConcatOptionalParameters;
    }

    public void setPutConcatOptionalParameters(final boolean putConcatOptionalParameters) {
        this.putConcatOptionalParameters = putConcatOptionalParameters;
    }


}