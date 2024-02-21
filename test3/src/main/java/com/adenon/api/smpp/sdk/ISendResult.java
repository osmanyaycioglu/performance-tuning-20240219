package com.adenon.api.smpp.sdk;

public interface ISendResult {

    public static final int ERROR_CAUSE_FATAL_ERROR             = 1001;
    public static final int ERROR_CAUSE_PROTOCOL_ERROR          = 1002;
    public static final int ERROR_CAUSE_INVALID                 = 1003;
    public static final int ERROR_CAUSE_INTERNAL_ERROR          = 1004;
    public static final int ERROR_CAUSE_NO_CONNECTION_GROUP     = 1005;
    public static final int ERROR_CAUSE_NO_CONNECTED_CONNECTION = 1006;
    public static final int ERROR_CAUSE_CONNECTION_READONLY     = 1007;
    public static final int ERROR_CAUSE_NAME_NOT_VALID          = 1008;

    public ConnectionInformation getConnectionInformation();

    public long getTransactionId();

    public ESendResult getSendResult();

    public String getErrorDescription();

    public int getErrorCause();

}
