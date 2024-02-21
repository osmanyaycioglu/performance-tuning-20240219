package com.adenon.api.smpp.common;

public class SmppApiException extends Exception {

    private static final long    serialVersionUID             = 1212343462321983331L;


    public static final int      NULL                         = 0;
    public static final int      NOT_AVAILABLE                = 1;
    public static final int      NOT_APPLICABLE               = 2;
    public static final int      FATAL_ERROR                  = 3;
    public static final int      PROTOCOL_ERROR               = 4;
    public static final int      INVALID_PARAMETER            = 5;
    public static final int      MISSING_PARAMETER            = 6;
    public static final int      INVALID                      = 7;
    public static final int      READONLY                     = 8;
    public static final int      RETRY                        = 9;
    public static final int      UNSUPPORTED_PROTOCOL         = 10;
    public static final int      UNKNOWN                      = 11;
    public static final int      INTERNAL_ERROR               = 12;
    public static final int      LOGIN_ERROR                  = 13;

    public static final int      DOMAIN_SMPP_CONNECTION       = 0x100000;
    public static final int      DOMAIN_IOREACTOR             = 0x200000;
    public static final int      DOMAIN_SMSC                  = 0x300000;
    public static final int      DOMAIN_WAP_PUSH              = 0x400000;
    public static final int      DOMAIN_SMPP_SERVER           = 0x500000;
    public static final int      DOMAIN_SMPP_CHARATER_PROCESS = 0x600000;


    public static final String[] errorCodeDescriptions        = {
            "NULL",
            "NOT_AVAILABLE",
            "NOT_APPLICABLE",
            "FATAL_ERROR",
            "PROTOCOL_ERROR",
            "INVALID_PARAMETER",
            "MISSING_PARAMETER",
            "INVALID",
            "READONLY",
            "RETRY",
            "UNSUPPORTED_PROTOCOL",
            "UNKNOWN",
            "INTERNAL_ERROR",
            "LOGIN_ERROR"                                    };

    public static final String[] domainDescriptions           = {
            "DOMAIN_SMPP_CONNECTION",
            "DOMAIN_IOREACTOR",
            "DOMAIN_SMSC",
            "DOMAIN_WAP_PUSH",
            "DOMAIN_SMPP_SERVER",
            "DOMAIN_SMPP_CHARATER_PROCESS"                   };

    private int                  errorCode                    = -1;
    private String               errorDescription             = "";
    private int                  domain;

    public SmppApiException(final int err,
                            final String description) {
        this.setErrorCode(err);
        this.setErrorDescription(description);
    }

    public SmppApiException(final int err,
                            final int domain,
                            final String description) {
        this.setErrorCode(err);
        this.setErrorDescription(description);
        this.setDomain(domain);
    }

    public SmppApiException(final Exception exception) {
        super(exception);
        this.errorCode = SmppApiException.FATAL_ERROR;
        this.errorDescription = exception.getMessage();
    }

    @Override
    public String getMessage() {
        final int errorIndex = this.getErrorCode() & 0x0f;
        String retVal = "ERROR MESSAGE";
        try {
            retVal = SmppApiException.errorCodeDescriptions[errorIndex] + " - " + this.getErrorDescription();
        } catch (final Exception e) {
        }
        return retVal;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return this.errorDescription;
    }

    public void setErrorDescription(final String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public int getDomain() {
        return this.domain;
    }

    public void setDomain(final int domain) {
        this.domain = domain;
    }
}
