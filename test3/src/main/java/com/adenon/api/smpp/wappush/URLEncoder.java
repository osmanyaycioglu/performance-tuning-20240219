package com.adenon.api.smpp.wappush;

import com.adenon.api.smpp.common.SmppApiException;


public class URLEncoder {

    private final static int PROTOCOL_HTTP  = 0;
    private final static int PROTOCOL_HTTPS = 1;

    private final String     urlString;

    private int              protocolCode   = -1;
    private int              domainCode     = -1;
    private String           domainStr;
    private String           urlStr;
    private final int        base;

    public URLEncoder(final String urlString,
                      final int base) {
        this.urlString = urlString;
        this.base = base;
    }

    public void decode() throws Exception {
        int protocolType = URLEncoder.PROTOCOL_HTTP;
        final int indexOfProtocol = this.urlString.indexOf("://");
        String bareUrl = null;
        if (indexOfProtocol > 0) {
            final String protocolString = this.urlString.substring(0, indexOfProtocol).toLowerCase();
            if ("https".equals(protocolString)) {
                protocolType = URLEncoder.PROTOCOL_HTTPS;
            } else if ("http".equals(protocolString)) {
                protocolType = URLEncoder.PROTOCOL_HTTP;
            } else {
                if (protocolString.contains(":")) {
                    throw new SmppApiException(SmppApiException.UNSUPPORTED_PROTOCOL, SmppApiException.DOMAIN_WAP_PUSH, "We dont support protocol : "
                                                                                                                        + protocolString);
                } else {
                    protocolType = URLEncoder.PROTOCOL_HTTP;
                }
            }
            bareUrl = this.urlString.substring(indexOfProtocol + 3);
        } else {
            bareUrl = this.urlString;
        }
        if (bareUrl.toLowerCase().startsWith("www.")) {
            switch (protocolType) {
                case PROTOCOL_HTTP:
                    this.protocolCode = WAPPushCommon.WBXML_INDICATION_URL_HREF_HTTP_WWW + this.base;
                    break;
                case PROTOCOL_HTTPS:
                    this.protocolCode = WAPPushCommon.WBXML_INDICATION_URL_HREF_HTTPS_WWW + this.base;
                    break;
                default:
                    throw new SmppApiException(SmppApiException.UNSUPPORTED_PROTOCOL, SmppApiException.DOMAIN_WAP_PUSH, "We dont support protocol : "
                                                                                                                        + protocolType);
            }
            bareUrl = bareUrl.substring(4);
        } else {
            switch (protocolType) {
                case PROTOCOL_HTTP:
                    this.protocolCode = WAPPushCommon.WBXML_INDICATION_URL_HREF_HTTP + this.base;
                    break;
                case PROTOCOL_HTTPS:
                    this.protocolCode = WAPPushCommon.WBXML_INDICATION_URL_HREF_HTTPS + this.base;
                    break;
                default:
                    throw new SmppApiException(SmppApiException.UNSUPPORTED_PROTOCOL, SmppApiException.DOMAIN_WAP_PUSH, "We dont support protocol : "
                                                                                                                        + protocolType);
            }
        }
        final int indexOfDelim = bareUrl.indexOf("/");

        String serverType = null;
        if (indexOfDelim > 0) {
            serverType = bareUrl.substring(0, indexOfDelim);
            this.urlStr = bareUrl.substring(indexOfDelim + 1);
        } else {
            serverType = bareUrl;
        }
        final String lowerServerType = serverType.toLowerCase();
        if (lowerServerType.endsWith(".com")) {
            this.domainCode = WAPPushCommon.WBXML_SI_INDICATION_URL_COM;
        } else if (lowerServerType.endsWith(".edu")) {
            this.domainCode = WAPPushCommon.WBXML_SI_INDICATION_URL_EDU;
        } else if (lowerServerType.endsWith(".net")) {
            this.domainCode = WAPPushCommon.WBXML_SI_INDICATION_URL_NET;
        } else if (lowerServerType.endsWith(".org")) {
            this.domainCode = WAPPushCommon.WBXML_SI_INDICATION_URL_ORG;
        }
        if (this.domainCode > 0) {
            this.domainStr = serverType.substring(0, serverType.length() - 4);
        } else {
            this.urlStr = bareUrl;
        }

    }

    public int getProtocolCode() {
        return this.protocolCode;
    }

    public void setProtocolCode(final int protocolCode) {
        this.protocolCode = protocolCode;
    }

    public int getDomainCode() {
        return this.domainCode;
    }

    public void setDomainCode(final int domainCode) {
        this.domainCode = domainCode;
    }

    public String getDomainStr() {
        return this.domainStr;
    }

    public void setDomainStr(final String domainStr) {
        this.domainStr = domainStr;
    }

    public String getUrlStr() {
        return this.urlStr;
    }

    public void setUrlStr(final String urlStr) {
        this.urlStr = urlStr;
    }

    public static void main(final String[] args) {
        final URLEncoder encoder = new URLEncoder("http://wap.kaan.com.tr/kaan/zKaan.wml", WAPPushCommon.WBXML_SL_BASE);
        try {
            encoder.decode();
        } catch (final Exception e) {
            System.err.println(e);
        }
        System.out.println("Domain Str :" + encoder.getDomainStr());

    }

}
