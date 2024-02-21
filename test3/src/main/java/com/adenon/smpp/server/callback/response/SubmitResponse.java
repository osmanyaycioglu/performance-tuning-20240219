package com.adenon.smpp.server.callback.response;

import java.util.ArrayList;
import java.util.List;

import com.adenon.api.smpp.message.OptionalParameter;


public class SubmitResponse {

    private ESubmitResult           submitResult;
    private String                  messageId;
    private int                     extraResult;
    private List<OptionalParameter> optionalParameters;

    public SubmitResponse(final ESubmitResult submitResult,
                          final String messageId) {
        this.messageId = messageId;
        this.submitResult = submitResult;
    }

    public SubmitResponse(final int extraResult) {
        this.setExtraResult(extraResult);
    }

    public ESubmitResult getSubmitResult() {
        return this.submitResult;
    }


    public String getMessageId() {
        return this.messageId;
    }

    public void addOptionalParameter(final OptionalParameter optionalParameter) {
        if (optionalParameter == null) {
            return;
        }
        if (this.getOptionalParameters() == null) {
            this.setOptionalParameters(new ArrayList<OptionalParameter>());
        }
        this.getOptionalParameters().add(optionalParameter);
    }

    public int getExtraResult() {
        return extraResult;
    }

    public void setExtraResult(int extraResult) {
        this.extraResult = extraResult;
    }

    public List<OptionalParameter> getOptionalParameters() {
        return optionalParameters;
    }

    public void setOptionalParameters(List<OptionalParameter> optionalParameters) {
        this.optionalParameters = optionalParameters;
    }
}
