package com.adenon.api.smpp.wappush;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.sdk.EMessageType;
import com.adenon.api.smpp.sdk.WapPushBookmarkMessageDescriptor;


public class BookmarkMessage extends WSPPart implements IWapMessage {


    private final WapPushBookmarkMessageDescriptor wapPushBookmarkMessageDescriptor;

    public BookmarkMessage(final WapPushBookmarkMessageDescriptor wapPushBookmarkMessageDescriptor) {
        // super("application/x-wap-prov.browser-bookmarks", -1, (short) 0xC34F, (short) 0x23F0);
        super("application/x-wap-prov.browser-settings", -1, (short) 0xC34F, (short) 0xC002);
        this.wapPushBookmarkMessageDescriptor = wapPushBookmarkMessageDescriptor;
    }

    @Override
    public void encode(final ByteBuffer byteBuffer) throws Exception {

        byteBuffer.put((byte) WAPPushCommon.WBXML_ATTR_VERSION_NUMBER); // WBXML Version 1.2
        byteBuffer.put((byte) 0x01); // Unknown document type
        byteBuffer.put((byte) WAPPushCommon.WBXML_ATTR_CHARSET_UTF_8); // Charset UTF-8
        byteBuffer.put((byte) 0x00); // String Table Length ( = 0 )
        byteBuffer.put((byte) WAPPushCommon.WBXML_CHARACTERISTIC_LIST); // <CHARACTERISTIC-LIST> element, with content
        byteBuffer.put((byte) WAPPushCommon.WBXML_CHARACTERISTIC_LIST_CHARACTERISTIC); // <CHARACTERISTIC> element, with content and attributes
        byteBuffer.put((byte) WAPPushCommon.WBXML_CHARACTERISTIC_LIST_CHARACTERISTIC_TYPE_BOOKMARK); // attribute with value TYPE=BOOKMARK
        byteBuffer.put((byte) WAPPushCommon.WBXML_TAG_END); // end of <CHARACTERISTIC> attributes
        byteBuffer.put((byte) WAPPushCommon.WBXML_CHARACTERISTIC_LIST_PARAM); // <PARM> element with attributes
        byteBuffer.put((byte) WAPPushCommon.WBXML_CHARACTERISTIC_LIST_PARAM_NAME); // attribute with value NAME=NAME
        byteBuffer.put((byte) WAPPushCommon.WBXML_CHARACTERISTIC_LIST_PARAM_VALUE); // attribute VALUE
        byteBuffer.put((byte) WAPPushCommon.WBXML_ATTR_DIRECTIVE_INLINE_STRING_FOLLOWS); // Inline string follows
        byteBuffer.put(this.wapPushBookmarkMessageDescriptor.getName().getBytes("UTF-8"));
        byteBuffer.put((byte) WAPPushCommon.WBXML_STRING_END); // String end
        byteBuffer.put((byte) WAPPushCommon.WBXML_TAG_END); // end of </PARM> attributes
        byteBuffer.put((byte) WAPPushCommon.WBXML_CHARACTERISTIC_LIST_PARAM); // <PARM> element with attributes
        byteBuffer.put((byte) WAPPushCommon.WBXML_CHARACTERISTIC_LIST_PARAM_URL); // attribute with value NAME=URL
        byteBuffer.put((byte) WAPPushCommon.WBXML_CHARACTERISTIC_LIST_PARAM_VALUE); // attribute VALUE
        byteBuffer.put((byte) WAPPushCommon.WBXML_ATTR_DIRECTIVE_INLINE_STRING_FOLLOWS); // Inline string follows
        byteBuffer.put(this.wapPushBookmarkMessageDescriptor.getUrl().getBytes("UTF-8"));
        byteBuffer.put((byte) WAPPushCommon.WBXML_STRING_END); // String end
        byteBuffer.put((byte) WAPPushCommon.WBXML_TAG_END); // end of </PARM> attributes
        byteBuffer.put((byte) WAPPushCommon.WBXML_TAG_END); // end of </CHARACTERISTIC> element
        byteBuffer.put((byte) WAPPushCommon.WBXML_TAG_END); // end of </CHARACTERISTIC-LIST>


    }

    @Override
    public EMessageType getMessageType() {
        return EMessageType.WAPBookmark;
    }

}
