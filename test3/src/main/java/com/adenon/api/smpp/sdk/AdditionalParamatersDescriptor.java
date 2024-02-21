package com.adenon.api.smpp.sdk;

import java.util.List;

import com.adenon.api.smpp.message.OptionalParameter;


public class AdditionalParamatersDescriptor {

    private boolean                 requestDelivery;
    private boolean                 putConcatHeader;
    private int                     messageSchedulePeriod;
    private int                     validityPeriod;
    private List<OptionalParameter> optionalParameters;

    public boolean isRequestDelivery() {
        return this.requestDelivery;
    }

    public AdditionalParamatersDescriptor setRequestDelivery(final boolean requestDelivery) {
        this.requestDelivery = requestDelivery;
        return this;
    }

    public int getMessageSchedulePeriod() {
        return this.messageSchedulePeriod;
    }

    public AdditionalParamatersDescriptor setMessageSchedulePeriod(final int messageSchedulePeriod) {
        this.messageSchedulePeriod = messageSchedulePeriod;
        return this;
    }

    public int getValidityPeriod() {
        return this.validityPeriod;
    }

    public AdditionalParamatersDescriptor setValidityPeriod(final int validityPeriod) {
        this.validityPeriod = validityPeriod;
        return this;
    }

    public boolean isPutConcatHeader() {
        return this.putConcatHeader;
    }

    public AdditionalParamatersDescriptor setPutConcatHeader(final boolean putConcatHeader) {
        this.putConcatHeader = putConcatHeader;
        return this;
    }

    public List<OptionalParameter> getOptionalParameters() {
        return optionalParameters;
    }

    public void setOptionalParameters(List<OptionalParameter> optionalParameters) {
        this.optionalParameters = optionalParameters;
    }

}
