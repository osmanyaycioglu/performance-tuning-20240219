package com.adenon.api.smpp.common;

import java.nio.ByteBuffer;


public class PDUParser {

    public static String parsePDU(final ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return "BYTE BUFFER IS NULL !!!!!";
        }
        if (byteBuffer.limit() < 16) {
            return "WRONG PDU HEADER !!!!!";
        }
        byteBuffer.position(0);
        final byte[] byteArray = new byte[byteBuffer.limit()];
        byteBuffer.get(byteArray);
        byteBuffer.position(0);
        final StringBuilder stringBuilder = new StringBuilder(300);
        stringBuilder.append("HEADER ->");
        stringBuilder.append(CommonUtils.convertByteStringToHex(byteArray, 0, 16));
        stringBuilder.append(" -> |Size:");
        stringBuilder.append(CommonUtils.convertByteStringToHex(byteArray, 0, 4));
        stringBuilder.append("|Command:");
        stringBuilder.append(CommonUtils.convertByteStringToHex(byteArray, 4, 4));
        stringBuilder.append("|Status:");
        stringBuilder.append(CommonUtils.convertByteStringToHex(byteArray, 8, 4));
        stringBuilder.append("|Sequence:");
        stringBuilder.append(CommonUtils.convertByteStringToHex(byteArray, 12, 4));
        stringBuilder.append("\r");
        if (byteArray.length > 16) {
            stringBuilder.append("BODY ->");
            stringBuilder.append(CommonUtils.convertByteStringToHex(byteArray, 16, byteArray.length - 16));
        }
        return stringBuilder.toString();

    }

}
