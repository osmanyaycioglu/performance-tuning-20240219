package com.adenon.api.smpp.wappush;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.common.SmppApiException;
import com.adenon.api.smpp.sdk.EMessageType;
import com.adenon.api.smpp.sdk.WapPushSLMessageDescriptor;


public class ServiceLoadingMessage extends WSPPart implements IWapMessage {

    private final WapPushSLMessageDescriptor indicatorDescriptor;

    public ServiceLoadingMessage(final WapPushSLMessageDescriptor indicatorDescriptor) {
        super("application/vnd.wap.sic", 0xAE, (short) 0x0B84, (short) 0x23F0);
        this.indicatorDescriptor = indicatorDescriptor;
    }


    @Override
    public void encode(final ByteBuffer byteBuffer) throws Exception {

        byteBuffer.put((byte) WAPPushCommon.WBXML_ATTR_VERSION_NUMBER); // WBXML Version 1.2
        byteBuffer.put((byte) WAPPushCommon.WBXML_SL_1_0_PUBLIC_IDENTIFIER); // SI 1.0 Public Identifier
        byteBuffer.put((byte) WAPPushCommon.WBXML_ATTR_CHARSET_UTF_8); // Charset UTF-8
        byteBuffer.put((byte) 0x00); // String Table Length ( = 0 )
        byteBuffer.put((byte) WAPPushCommon.WBXML_SL_TAG_CONTENT); // <sl> (with content)

        if (!CommonUtils.checkStringIsEmpty(this.indicatorDescriptor.getHrefUrl())) {
            final URLEncoder urlEncoder = new URLEncoder(this.indicatorDescriptor.getHrefUrl(), WAPPushCommon.WBXML_SL_BASE);
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

            if (this.indicatorDescriptor.getActionType() != null) {
                byteBuffer.put((byte) this.indicatorDescriptor.getActionType().getValue());
            }
            byteBuffer.put((byte) WAPPushCommon.WBXML_TAG_END); // </sl>


        } else {
            throw new SmppApiException(SmppApiException.MISSING_PARAMETER, "Url string is Empty!");
        }
    }

    @Override
    public EMessageType getMessageType() {
        return EMessageType.WAPPushSI;
    }

}
