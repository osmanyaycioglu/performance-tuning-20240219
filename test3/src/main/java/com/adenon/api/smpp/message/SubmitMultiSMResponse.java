package com.adenon.api.smpp.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.sdk.ENumberPlanIndicator;
import com.adenon.api.smpp.sdk.ESendResult;
import com.adenon.api.smpp.sdk.ETypeOfNumber;

public class SubmitMultiSMResponse {

    private String                      messageIdentifier   = null;
    private int                         noUnsuccesses;
    private final List<UnsuccessfulSME> unsuccessfulSMEList = new ArrayList<UnsuccessfulSME>();
    private SubmitMultiSMMessage        requestMessage;
    private int                         commandStatus;

    public int getNoUnsuccesses() {
        return this.noUnsuccesses;
    }

    public void setNoUnsuccesses(final int noUnsuccesses) {
        this.noUnsuccesses = noUnsuccesses;
    }

    public void setMessageIdentifier(final String messageIdentifier) {
        this.messageIdentifier = messageIdentifier;
    }

    public List<UnsuccessfulSME> getUnsuccessfulSMEList() {
        return this.unsuccessfulSMEList;
    }

    public String getMessageIdentifier() {
        return this.messageIdentifier;
    }

    public SubmitMultiSMResponse() {
    }

    public ESendResult getSendResult() {
        return this.getRequestMessage().getSendResult();
    }

    public void parseMessage(final ByteBuffer byteBuffer) throws Exception {
        if (byteBuffer.hasRemaining()) {
            byte[] temp = new byte[65];
            final int charCount = CommonUtils.getCOctetString(temp, byteBuffer);
            this.setMessageIdentifier(new String(temp, 0, charCount));
            temp = null;

            if (byteBuffer.hasRemaining()) {
                this.noUnsuccesses = byteBuffer.get();// set number of unsuccess.

                for (int i = 0; i < this.noUnsuccesses; i++) {
                    final UnsuccessfulSME unsuccessfulSME = new UnsuccessfulSME();
                    unsuccessfulSME.setTon(ETypeOfNumber.getTon(byteBuffer.get()));// set ton
                    unsuccessfulSME.setNpi(ENumberPlanIndicator.getNpi(byteBuffer.get()));// set npi
                    // set address
                    byte[] numberBytes = new byte[21];
                    final int charCount1 = CommonUtils.getCOctetString(numberBytes, byteBuffer);
                    unsuccessfulSME.setNumber(new String(numberBytes, 0, charCount1));
                    numberBytes = null;
                    // set error code
                    unsuccessfulSME.setErrorCode(byteBuffer.getInt());

                    // add unsuccessfulSME to a list
                    this.unsuccessfulSMEList.add(unsuccessfulSME);
                }
            }
        }

    }

    public SubmitMultiSMMessage getRequestMessage() {
        return this.requestMessage;
    }

    public void setRequestMessage(final SubmitMultiSMMessage requestMessage) {
        this.requestMessage = requestMessage;
    }

    public int getCommandStatus() {
        return this.commandStatus;
    }

    public void setCommandStatus(final int commandStatus) {
        this.commandStatus = commandStatus;
    }
}