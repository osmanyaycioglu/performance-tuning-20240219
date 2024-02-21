package com.adenon.api.smpp.common;

public class CommonParameters {

    public static final int LOG_TYPE_ONE_AND_ONLY                               = 0;
    public static final int LOG_TYPE_CLIENT                                     = 1;
    public static final int LOG_TYPE_HOST                                       = 2;

    public static final int TYPE_SMS                                            = 1;
    public static final int TYPE_USSD                                           = 2;

    public static final int DATA_CODING_SMSC_DEFAULT                            = 0x00;
    public static final int DATA_CODING_ASCII                                   = 0x01;
    public static final int DATA_CODING_ISO_8859_1                              = 0x03;
    public static final int DATA_CODING_BINARY                                  = 0x04;
    public static final int DATA_CODING_UCS2                                    = 0x08;

    public static final int TON_UNKNOWN                                         = 0x00;
    public static final int TON_INTERNATIONAL                                   = 0x01;
    public static final int TON_NATIONAL                                        = 0x02;
    public static final int TON_NEWORK_SPECIFIC                                 = 0x03;
    public static final int TON_SUBSCRIBER_NUMBER                               = 0x04;
    public static final int TON_ALPHANUMERIC                                    = 0x05;
    public static final int TON_ABBREVIATED                                     = 0x06;

    public static final int NDI_UNKNOWN                                         = 0x00;
    public static final int NDI_ISDN                                            = 0x01;
    public static final int NDI_DATA                                            = 0x03;
    public static final int NDI_TELEX                                           = 0x04;
    public static final int NDI_LAN_MOBILE                                      = 0x06;
    public static final int NDI_NATIONAL                                        = 0x08;
    public static final int NDI_PRIVATE                                         = 0x09;
    public static final int NDI_ERMES                                           = 0x0A;
    public static final int NDI_INTERNET_IP                                     = 0x0D;
    public static final int NPI_WAP_CLIENT_ID                                   = 0x12;
    // Byte size for msg types
    public static final int BYTE_COUNT_FOR_SMS                                  = 140;
    public static final int BYTE_COUNT_FOR_USSD                                 = 160;

    public static final int BYTE_COUNT_FOR_7BIT_SMS                             = 160;
    public static final int BYTE_COUNT_FOR_CONCAT_7BIT_SMS                      = 153;

    public static final int BYTE_COUNT_FOR_7BIT_TURKISH_SINGLE_SHIFT_SMS        = 155;
    public static final int BYTE_COUNT_FOR_7BIT_CONCAT_TURKISH_SINGLE_SHIFT_SMS = 149;

    public static final int BYTE_COUNT_FOR_UC8_SMS                              = 140;
    public static final int BYTE_COUNT_FOR_CONCAT_UC8_SMS                       = 134;

}
