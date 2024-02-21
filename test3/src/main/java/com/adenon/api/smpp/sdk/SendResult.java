package com.adenon.api.smpp.sdk;

import com.adenon.api.smpp.message.SubmitSMMessage;


public class SendResult implements ISMSSendResult {

    private ConnectionInformation connectionInformation;
    private long                  transactionId;
    private ESendResult           sendResult;
    private String                errorDescription;
    private int                   errorCause;
    private SubmitSMMessage       submitSMMessage;

    public SendResult(final ConnectionInformation connectionInformation,
                      final long pTransactionId,
                      final SubmitSMMessage submitSMMessage,
                      final ESendResult sendResult,
                      final int errorCause,
                      final String errorDescription) {
        this.connectionInformation = connectionInformation;
        this.transactionId = pTransactionId;
        this.submitSMMessage = submitSMMessage;
        this.sendResult = sendResult;
        this.errorCause = errorCause;
        this.errorDescription = errorDescription;
    }

    public SendResult(final ConnectionInformation connectionInformation,
                      final long pTransactionId,
                      final SubmitSMMessage submitSMMessage) {
        this(connectionInformation, pTransactionId, submitSMMessage, ESendResult.SUCCESS, 0, null);
    }

    public SendResult(final int errorCause,
                      final String errorDescription) {
        this(null, 0, null, ESendResult.ERROR, errorCause, errorDescription);
    }

    public SendResult(final ESendResult sendResult,
                      final int errorCause,
                      final String errorDescription) {
        this(null, 0, null, sendResult, errorCause, errorDescription);
    }

    public SendResult() {

    }

    @Override
    public ConnectionInformation getConnectionInformation() {
        return this.connectionInformation;
    }

    @Override
    public ESendResult getSendResult() {
        return this.sendResult;
    }

    public void setSendResult(final ESendResult sendResult) {
        this.sendResult = sendResult;
    }

    @Override
    public String getErrorDescription() {
        return this.errorDescription;
    }

    public void setErrorDescription(final String sendErrorDescription) {
        this.errorDescription = sendErrorDescription;
    }

    @Override
    public int getErrorCause() {
        return this.errorCause;
    }

    public void setErrorCause(final int errorCause) {
        this.errorCause = errorCause;
    }

    @Override
    public long getTransactionId() {
        return this.transactionId;
    }

    @Override
    public SubmitSMMessage getMessage() {
        return this.submitSMMessage;
    }

}
