package com.adenon.api.smpp.wappush;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.common.SmppApiException;
import com.adenon.api.smpp.sdk.EMessageType;
import com.adenon.api.smpp.sdk.WapPushSIMessageDescriptor;

public class ServiceIndicationMessage extends WSPPart implements IWapMessage {

    private final WapPushSIMessageDescriptor indicatorDescriptor;

    public ServiceIndicationMessage(final WapPushSIMessageDescriptor indicatorDescriptor) {
        super("application/vnd.wap.sic", 0xAE, (short) 0x0B84, (short) 0x23F0);
        this.indicatorDescriptor = indicatorDescriptor;
    }


    @Override
    public void encode(final ByteBuffer byteBuffer) throws Exception {

        byteBuffer.put((byte) WAPPushCommon.WBXML_ATTR_VERSION_NUMBER); // WBXML Version 1.2
        byteBuffer.put((byte) WAPPushCommon.WBXML_SI_TAG_PUBLIC_IDENTIFER); // SI 1.0 Public Identifier
        byteBuffer.put((byte) WAPPushCommon.WBXML_ATTR_CHARSET_UTF_8); // Charset UTF-8
        byteBuffer.put((byte) 0x00); // String Table Length ( = 0 )
        byteBuffer.put((byte) WAPPushCommon.WBXML_SI_TAG_CONTENT); // <si> (with content)
        byteBuffer.put((byte) WAPPushCommon.WBXML_SI_TAG_INDICATION); // <indication> (with content and attributes)

        if (!CommonUtils.checkStringIsEmpty(this.indicatorDescriptor.getHrefUrl())) {
            final URLEncoder urlEncoder = new URLEncoder(this.indicatorDescriptor.getHrefUrl(), WAPPushCommon.WBXML_SI_BASE);
            urlEncoder.decode();
            byteBuffer.put((byte) urlEncoder.getProtocolCode());
            if (urlEncoder.getDomainCode() > -1) {
                byteBuffer.put((byte) WAPPushCommon.WBXML_ATTR_DIRECTIVE_INLINE_STRING_FOLLOWS);
                byteBuffer.put(urlEncoder.getDomainStr().getBytes("UTF-8"));
                byteBuffer.put((byte) 0x00);
                byteBuffer.put((byte) urlEncoder.getDomainCode());
            }
            if (!CommonUtils.checkStringIsEmpty(urlEncoder.getUrlStr())) {
                byteBuffer.put((byte) WAPPushCommon.WBXML_ATTR_DIRECTIVE_INLINE_STRING_FOLLOWS);
                byteBuffer.put(urlEncoder.getUrlStr().getBytes("UTF-8"));
                byteBuffer.put((byte) 0x00);
            }

            if (!CommonUtils.checkStringIsEmpty(this.indicatorDescriptor.getServiceIndicatorId())) {
                byteBuffer.put((byte) WAPPushCommon.WBXML_SI_INDICATION_ATTR_ID);
                byteBuffer.put((byte) WAPPushCommon.WBXML_ATTR_DIRECTIVE_INLINE_STRING_FOLLOWS);
                byteBuffer.put(this.indicatorDescriptor.getServiceIndicatorId().getBytes("UTF-8"));
                byteBuffer.put((byte) 0x00);
            }

            if (this.indicatorDescriptor.getCreationDate() > 0) {
                final byte[] dateValue = CommonUtils.convertDateToWBXML(this.indicatorDescriptor.getCreationDate());
                byteBuffer.put((byte) WAPPushCommon.WBXML_SI_INDICATION_ATTR_CREATED);
                byteBuffer.put((byte) WAPPushCommon.WBXML_ATTR_DIRECTIVE_OPAQUE_DATA_FOLLOWS);
                byteBuffer.put((byte) dateValue.length);
                byteBuffer.put(dateValue);
            }

            if (this.indicatorDescriptor.getSiExpiryDate() > 0) {
                final byte[] dateValue = CommonUtils.convertDateToWBXML(this.indicatorDescriptor.getSiExpiryDate());
                byteBuffer.put((byte) WAPPushCommon.WBXML_SI_INDICATION_ATTR_EXPIRES);
                byteBuffer.put((byte) WAPPushCommon.WBXML_ATTR_DIRECTIVE_OPAQUE_DATA_FOLLOWS);
                byteBuffer.put((byte) dateValue.length);
                byteBuffer.put(dateValue);
            }

            if (this.indicatorDescriptor.getActionType() != null) {
                byteBuffer.put((byte) this.indicatorDescriptor.getActionType().getValue());
            }
            byteBuffer.put((byte) WAPPushCommon.WBXML_TAG_END);


            if (!CommonUtils.checkStringIsEmpty(this.indicatorDescriptor.getText())) {
                byteBuffer.put((byte) WAPPushCommon.WBXML_ATTR_DIRECTIVE_INLINE_STRING_FOLLOWS);
                byteBuffer.put(this.indicatorDescriptor.getText().getBytes("UTF-8"));
                byteBuffer.put((byte) 0x00);
            }

            byteBuffer.put((byte) WAPPushCommon.WBXML_TAG_END); // </indication>
            byteBuffer.put((byte) WAPPushCommon.WBXML_TAG_END); // </si>
        } else {
            throw new SmppApiException(SmppApiException.MISSING_PARAMETER, "Url string is Empty!");
        }
    }

    @Override
    public EMessageType getMessageType() {
        return EMessageType.WAPPushSI;
    }

}
