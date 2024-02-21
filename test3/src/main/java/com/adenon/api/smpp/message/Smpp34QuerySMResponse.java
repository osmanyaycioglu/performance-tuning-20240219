package com.adenon.api.smpp.message;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.sdk.ESendResult;


public class Smpp34QuerySMResponse {

    private ESendResult sendResult;

    private String      messageIdentifier = null;
    private String      finalDate         = null;
    private int         messageState;
    private int         errorCode;
    private final int   sequenceNumber;


    public Smpp34QuerySMResponse(final int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void parseMessage(final ByteBuffer byteBuffer) throws Exception {
        if (byteBuffer.hasRemaining()) {
            byte[] messageIdTemp = new byte[65];
            byte[] finalDateTemp = new byte[17];

            final int charCount1 = CommonUtils.getCOctetString(messageIdTemp, byteBuffer);
            this.setMessageIdentifier(new String(messageIdTemp, 0, charCount1));

            final int charCount2 = CommonUtils.getCOctetString(finalDateTemp, byteBuffer);
            this.setFinalDate(new String(finalDateTemp, 0, charCount2));

            this.setMessageState(byteBuffer.get());
            this.setErrorCode(byteBuffer.get());

            messageIdTemp = null;
            finalDateTemp = null;
        }
    }

    public int getSequenceNumber() {
        return this.sequenceNumber;
    }

    public String getMessageIdentifier() {
        return this.messageIdentifier;
    }


    public void setMessageIdentifier(final String messageIdentifier) {
        this.messageIdentifier = messageIdentifier;
    }


    public String getFinalDate() {
        return this.finalDate;
    }


    public void setFinalDate(final String finalDate) {
        this.finalDate = finalDate;
    }


    public int getMessageState() {
        return this.messageState;
    }


    public void setMessageState(final int messageState) {
        this.messageState = messageState;
    }


    public int getErrorCode() {
        return this.errorCode;
    }


    public void setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
    }

    public ESendResult getSendResult() {
        return this.sendResult;
    }

    public void setSendResult(final ESendResult sendResult) {
        this.sendResult = sendResult;
    }


}
