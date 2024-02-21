package com.adenon.smpp.server.core;

import com.adenon.api.smpp.message.DeliverSMMessage;


public interface IDeliveryResult {

    public static final int ERROR_CAUSE_FATAL_ERROR         = 1;
    public static final int ERROR_CAUSE_PROTOCOL_ERROR      = 2;
    public static final int ERROR_CAUSE_RETRY               = 3;
    public static final int ERROR_CAUSE_ERROR               = 4;
    public static final int ERROR_CAUSE_INTERNAL_ERROR      = 5;
    public static final int ERROR_CAUSE_NO_CONNECTION_GROUP = 6;
    public static final int ERROR_CAUSE_NO_CONNECTION       = 7;
    public static final int ERROR_CAUSE_CONNECTION_READONLY = 8;
    public static final int ERROR_CAUSE_HOST_NAME_NOT_VALID = 9;

    public EDeliveryResult getDeliveryResult();

    public String getErrorDescription();

    public int getErrorCause();

    public DeliverSMMessage getMessage();

    public long getTransactionId();


}
