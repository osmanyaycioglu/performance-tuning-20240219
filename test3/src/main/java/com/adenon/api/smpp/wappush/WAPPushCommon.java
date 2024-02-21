package com.adenon.api.smpp.wappush;

public class WAPPushCommon {

    public static final int WBXML_ATTR_VERSION_NUMBER                              = 0x02;
    public static final int WBXML_ATTR_CHARSET_UTF_8                               = 0x6a;

    public static final int WBXML_TAG_END                                          = 0x01;

    public static final int WBXML_CHARACTERISTIC_LIST                              = 0x45;
    public static final int WBXML_CHARACTERISTIC_LIST_CHARACTERISTIC               = 0xc6;
    public static final int WBXML_CHARACTERISTIC_LIST_CHARACTERISTIC_TYPE_BOOKMARK = 0x7F;
    public static final int WBXML_CHARACTERISTIC_LIST_PARAM                        = 0x87;
    public static final int WBXML_CHARACTERISTIC_LIST_PARAM_NAME                   = 0x15;
    public static final int WBXML_CHARACTERISTIC_LIST_PARAM_VALUE                  = 0x11;
    public static final int WBXML_CHARACTERISTIC_LIST_PARAM_URL                    = 0x17;


    public static final int WBXML_ATTR_DIRECTIVE_OPAQUE_DATA_FOLLOWS               = 0xC3;
    public static final int WBXML_ATTR_DIRECTIVE_INLINE_STRING_FOLLOWS             = 0x03;


    public static final int WBXML_SI_TAG_INDICATION                                = 0xc6;
    // <si> tag with content
    public static final int WBXML_SI_TAG_CONTENT                                   = 0x45;
    public static final int WBXML_SI_TAG_PUBLIC_IDENTIFER                          = 0x05;

    public static final int WBXML_SI_INDICATION_ATTR_ID                            = 0x11;
    public static final int WBXML_SI_INDICATION_ATTR_EXPIRES                       = 0x10;
    public static final int WBXML_SI_INDICATION_ATTR_CREATED                       = 0x0a;


    public static final int WBXML_SI_INDICATION_URL_COM                            = 0x85;
    public static final int WBXML_SI_INDICATION_URL_EDU                            = 0x86;
    public static final int WBXML_SI_INDICATION_URL_NET                            = 0x87;
    public static final int WBXML_SI_INDICATION_URL_ORG                            = 0x88;

    public static final int WBXML_SI_BASE                                          = 0x0b;
    public static final int WBXML_SL_BASE                                          = 0x08;

    public static final int WBXML_INDICATION_URL_HREF                              = 0x00;
    public static final int WBXML_INDICATION_URL_HREF_HTTP                         = 0x01;
    public static final int WBXML_INDICATION_URL_HREF_HTTP_WWW                     = 0x02;
    public static final int WBXML_INDICATION_URL_HREF_HTTPS                        = 0x03;
    public static final int WBXML_INDICATION_URL_HREF_HTTPS_WWW                    = 0x04;


    public static final int WBXML_VERSION_1_2                                      = 0x02;
    public static final int WBXML_SI_1_0_PUBLIC_IDENTIFIER                         = 0x05;
    public static final int WBXML_SL_1_0_PUBLIC_IDENTIFIER                         = 0x06;
    public static final int WBXML_CHARSET_UTF8                                     = 0x6A;

    public static final int WBXML_STRING_END                                       = 0x00;
    // <sl> tag with content
    public static final int WBXML_SL_TAG_CONTENT                                   = 0x85;
    // <indication> tag with content and attributes
    public static final int WBXML_INDICATION_TAG_CONTENT_AND_ATTRIBUTES            = 0xC6;

    // href protocol constants
    public static final int WBXML_HREF_UNKNOWN                                     = 0x0B;

    public static final int WBXML_HREF_HTTP                                        = 0x0C;

    public static final int WBXML_HREF_HTTP_WWW                                    = 0x0D;

    public static final int WBXML_HREF_HTTPS                                       = 0x0E;

    public static final int WBXML_HREF_HTTPS_WWW                                   = 0x0F;

    // href domain constants
    public static final int WBXML_DOMAIN_COM                                       = 0x85;

    public static final int WBXML_DOMAIN_EDU                                       = 0x86;

    public static final int WBXML_DOMAIN_NET                                       = 0x87;

    public static final int WBXML_DOMAIN_ORG                                       = 0x88;


    public static final int PUSH_CREATED                                           = 0x0A;
    public static final int PUSH_EXPIRES                                           = 0x10;
    public static final int PUSH_SI_ID                                             = 0x11;
    public static final int PUSH_CLASS                                             = 0x12;
    public static final int PUSH_SIGNAL_NONE                                       = 0x05;
    public static final int PUSH_SIGNAL_LOW                                        = 0x06;
    public static final int PUSH_SIGNAL_MEDIUM                                     = 0x07;
    public static final int PUSH_SIGNAL_HIGH                                       = 0x08;
    public static final int PUSH_SIGNAL_DELETE                                     = 0x09;

}
