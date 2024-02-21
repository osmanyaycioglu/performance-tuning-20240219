package com.adenon.smpp.server.core;

import com.adenon.api.smpp.message.DeliverSMMessage;


public class DeliveryResult implements IDeliveryResult {

    private EDeliveryResult  deliveryResult;
    private String           errorDescription;
    private int              errorCause;
    private DeliverSMMessage message;
    private long             transactionId;

    public DeliveryResult(final EDeliveryResult deliveryResult,
                          final int errorCause,
                          final String errorDescription,
                          final DeliverSMMessage message,
                          final long transactionId) {
        this.deliveryResult = deliveryResult;
        this.errorCause = errorCause;
        this.errorDescription = errorDescription;
        this.message = message;
        this.transactionId = transactionId;
    }


    @Override
    public EDeliveryResult getDeliveryResult() {
        return this.deliveryResult;
    }

    @Override
    public String getErrorDescription() {
        return this.errorDescription;
    }

    @Override
    public int getErrorCause() {
        return this.errorCause;
    }

    @Override
    public DeliverSMMessage getMessage() {
        return this.message;
    }


    @Override
    public long getTransactionId() {
        return this.transactionId;
    }

    public void setDeliveryResult(final EDeliveryResult deliveryResult) {
        this.deliveryResult = deliveryResult;
    }


    public void setErrorDescription(final String errorDescription) {
        this.errorDescription = errorDescription;
    }


    public void setErrorCause(final int errorCause) {
        this.errorCause = errorCause;
    }


    public void setMessage(final DeliverSMMessage message) {
        this.message = message;
    }

    public void setTransactionId(final long transactionId) {
        this.transactionId = transactionId;
    }


}
