package com.adenon.api.smpp.common;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.adenon.library.common.utils.ETime;

public class CommonUtils {

    private static final char[] nibbleToChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public static void createAckHeader(final ByteBuffer buffer,
                                       final int msgType,
                                       final int sequence,
                                       final int commandStatus) {
        buffer.putInt(16);
        buffer.putInt(msgType);
        buffer.putInt(commandStatus);
        buffer.putInt(sequence);
    }

    public static void createHeader(final ByteBuffer buffer,
                                    final int msgType,
                                    final int sequence,
                                    final int commandStatus) {
        buffer.putInt(0);
        buffer.putInt(msgType);
        buffer.putInt(commandStatus);
        buffer.putInt(sequence);
    }

    public static void createHeader(final ByteBuffer buffer,
                                    final int msgType,
                                    final int sequence) {
        buffer.putInt(0);
        buffer.putInt(msgType);
        buffer.putInt(0);
        buffer.putInt(sequence);
    }

    public static void setLength(final ByteBuffer buffer) {
        final int poz = buffer.position();
        buffer.position(0);
        buffer.putInt(poz);
        buffer.position(poz);
    }

    public static int getIntegerWithLen(final int len,
                                        final ByteBuffer byteBuffer) {
        int retVal = 0;
        if (len == 1) {
            retVal = 0xFF & byteBuffer.get();
        } else if (len == 2) {
            retVal = 0xFFFF & byteBuffer.getShort();
        } else if (len == 4) {
            retVal = byteBuffer.getInt();
        } else {
            byteBuffer.position(byteBuffer.position() + len);
        }

        return retVal;

    }

    public static String getDate(final long dateSec,
                                 final SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(dateSec)) + "000R";
    }

    public static void putStringBytesToOptBuffer(final int tag,
                                                 final byte[] bytes,
                                                 final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) tag);
        byteBuffer.putShort((short) bytes.length);
        byteBuffer.put(bytes);
    }

    public static void putShortToOptBuffer(final int tag,
                                           final short val,
                                           final ByteBuffer byteBuffer) {
    	for (int i = 0; i < 200; i++) {
    		String bytesToHexFormated = CommonUtils.bytesToHexFormated(byteBuffer);
    		if (bytesToHexFormated.contains("00AA00")){
    			System.out.println("hello there");
    		}
		}

    	byteBuffer.putShort((short) tag);
        byteBuffer.putShort((short) 2);
        byteBuffer.putShort(val);
    }

    public static void putByteToOptBuffer(final int tag,
                                          final byte val,
                                          final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) tag);
        byteBuffer.putShort((short) 1);
        byteBuffer.put(val);
    }

    public static void putCStringBytesToOptBuffer(final int tag,
                                                  final byte[] bytes,
                                                  final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) tag);
        byteBuffer.putShort((short) (bytes.length + 1));
        byteBuffer.put(bytes);
        byteBuffer.put((byte) 0x0);
    }

    public static void getOctetString(final byte[] str,
                                      final ByteBuffer byteBuffer,
                                      final int len) {
        for (int i = 0; i < len; i++) {
            str[i] = byteBuffer.get();
        }
    }

    public static void getOctetStringUnicode(final char[] str,
                                             final ByteBuffer byteBuffer,
                                             final int len) {
        for (int i = 0; i < len; i++) {
            str[i] = byteBuffer.getChar();
        }
    }

    public static byte[] getCOctetString(final ByteBuffer byteBuffer,
                                         final int index) {
        byte[] retVal;
        int temp_index = index;
        while ((byteBuffer.get(temp_index) != 0) && (byteBuffer.limit() > temp_index)) {
            temp_index++;
        }
        retVal = new byte[temp_index - index];
        temp_index = 0;
        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = byteBuffer.get(index + i);
        }
        return retVal;
    }

    public static String getOctetStringUnicodeEx(final ByteBuffer byteBuffer,
                                                 final int len) {
        final StringBuilder builder = new StringBuilder(len + 5);
        for (int i = 0; i < len; i++) {
            builder.append(byteBuffer.getChar());
        }
        return builder.toString();
    }

    public static String getOctetStringEx(final ByteBuffer byteBuffer,
                                          final int len) {
        final StringBuilder builder = new StringBuilder(len + 5);
        for (int i = 0; i < len; i++) {
            final byte _bb = byteBuffer.get();
            final char _cc = (char) (0x00ff & (char) _bb);
            builder.append(_cc);
        }
        return builder.toString();
    }

    public static String getOctetStringEx(final ByteBuffer byteBuffer,
                                          final int capacity,
                                          final int len) {
        final StringBuilder builder = new StringBuilder(capacity);
        byte terminate_byte;
        for (int i = 0; i < len; i++) {
            terminate_byte = byteBuffer.get();
            if (terminate_byte == 0) {
                break;
            }
            builder.append((char) terminate_byte);
            if (byteBuffer.limit() == byteBuffer.position()) {
                break;
            }
        }
        return builder.toString();
    }

    public static String getCOctetStringEx(final ByteBuffer byteBuffer,
                                           final int capacity) {
        final StringBuilder builder = new StringBuilder(capacity);
        byte terminate_byte;
        while ((terminate_byte = byteBuffer.get()) != 0) {
            builder.append((char) terminate_byte);
            if (byteBuffer.limit() == byteBuffer.position()) {
                break;
            }
        }
        return builder.toString();
    }

    public static int getCOctetString(final byte[] str,
                                      final ByteBuffer byteBuffer) {
        byte byteRead;
        int index = 0;
        while ((byteRead = byteBuffer.get()) != 0) {
            str[index] = byteRead;
            index++;
        }
        return index;
    }

    public static String getSmppCommandDescription(final int msgId) {
        switch (msgId) {
            case Smpp34Constants.MSG_GEN_NACK:
                return "{General Nack}";
            case Smpp34Constants.MSG_BIND_TRANSMITTER_RESP:
                return "{Bind Transmitter Response}";
            case Smpp34Constants.MSG_BIND_TRANSCVR_RESP:
                return "{Bind Trans&Receiver Response}";
            case Smpp34Constants.MSG_QUERY_SM_RESP:
                return "{QuerySM Response}";
            case Smpp34Constants.MSG_SUBMIT_SM_RESP:
                return "{SubmitSM Response}";
            case Smpp34Constants.MSG_DATA_SM_RESP:
                return "{DataSM Response}";
            case Smpp34Constants.MSG_UNBIND_RESP:
                return "{Unbind Response}";
            case Smpp34Constants.MSG_BIND_RECEIVER_RESP:
                return "{Bind Receiver Response}";
            case Smpp34Constants.MSG_REPLACE_SM_RESP:
                return "{ReplaceSM Response}";
            case Smpp34Constants.MSG_CANCEL_SM_RESP:
                return "{CancelSM Response}";
            case Smpp34Constants.MSG_ENQUIRE_LINK_RESP:
                return "{Enquire Link Response}";
            case Smpp34Constants.MSG_DELIVER_SM_RESP:
                return "{DeliverSM Response}";
            case Smpp34Constants.MSG_DELIVER_SM:
                return "{DeliverSM}";
            case Smpp34Constants.MSG_SUBMIT_SM:
                return "{SubmitSM}";
            case Smpp34Constants.MSG_ENQUIRE_LINK:
                return "{Enquire Link}";
            case Smpp34Constants.MSG_ALERT_NOTIFICATION:
                return "{Alert}";
            case Smpp34Constants.MSG_BIND_TRANSCVR:
                return "{Bind Trans&Receiver}";
            case Smpp34Constants.MSG_BIND_RECEIVER:
                return "{Bind Receiver}";
            case Smpp34Constants.MSG_BIND_TRANSMITTER:
                return "{Bind Transmitter}";
            default:
                return "{!UNKNOWN!}";
        }
    }

    public static int getIndex(final char car) throws SmppApiException {
        for (int i = 0; i < CommonUtils.nibbleToChar.length; i++) {
            if (car == CommonUtils.nibbleToChar[i]) {
                return i;
            }
        }
        throw new SmppApiException(SmppApiException.FATAL_ERROR, "hexadecimal char unrecognized !!");
    }

    public static byte strToByte(final char[] cs,
                                 final int startIndex) throws SmppApiException {
        byte retByte = 0;
        int index = CommonUtils.getIndex(cs[startIndex]);
        index = index << 4;
        retByte = (byte) index;
        index = CommonUtils.getIndex(cs[startIndex + 1]);
        retByte |= (byte) index;
        return retByte;
    }

    public static byte strToByte(final String byteStr) throws SmppApiException {
        final char[] cs = new char[2];
        byte retByte = 0;
        byteStr.getChars(0, 2, cs, 0);
        int index = CommonUtils.getIndex(cs[0]);
        index = index << 4;
        retByte = (byte) index;
        index = CommonUtils.getIndex(cs[1]);
        retByte |= (byte) index;
        return retByte;
    }

    public static byte[] strToByteAll(final String byteStr,
                                      final int start,
                                      final int length) throws SmppApiException {
        if (byteStr == null) {
            throw new SmppApiException(SmppApiException.FATAL_ERROR, "hexadecimal string is null!!");
        }
        final char[] cs = new char[byteStr.length()];
        byteStr.toUpperCase().getChars(start, length, cs, 0);
        if ((byteStr.length() % 2) == 1) {
            throw new SmppApiException(SmppApiException.FATAL_ERROR, "hexadecimal string is length is not even!!");
        }
        final byte[] bytes = new byte[cs.length / 2];
        int counter = 0;
        for (int i = 0; i < cs.length; i += 2) {
            bytes[counter] = CommonUtils.strToByte(cs, i);
            counter++;
        }
        return bytes;
    }

    public static byte[] strToByteAll(final String byteStr) throws SmppApiException {
        if (byteStr == null) {
            throw new SmppApiException(SmppApiException.FATAL_ERROR, "hexadecimal string is null!!");
        }
        final char[] cs = new char[byteStr.length()];
        byteStr.getChars(0, byteStr.length(), cs, 0);
        if ((byteStr.length() % 2) == 1) {
            throw new SmppApiException(SmppApiException.FATAL_ERROR, "hexadecimal string is length is not even!!");
        }
        final byte[] bytes = new byte[cs.length / 2];
        int counter = 0;
        for (int i = 0; i < cs.length; i += 2) {
            bytes[counter] = CommonUtils.strToByte(cs, i);
            counter++;
        }
        return bytes;
    }

    public static byte[] strToByteAllWithSpace(final String byteStr) throws SmppApiException {
        if (byteStr == null) {
            throw new SmppApiException(SmppApiException.FATAL_ERROR, "hexadecimal string is null!!");
        }
        final char[] cs = new char[byteStr.length()];
        byteStr.getChars(0, byteStr.length(), cs, 0);
        final List<Byte> byteList = new ArrayList<Byte>(cs.length);
        for (int i = 0; i < cs.length; i++) {
            if (cs[i] != ' ') {
                final byte converted = CommonUtils.strToByte(cs, i);
                byteList.add(converted);
                i++;
            }
        }
        final byte[] bytes = new byte[byteList.size()];
        int count = 0;
        for (final Byte byteInput : byteList) {
            bytes[count] = byteInput;
            count++;
        }
        return bytes;
    }

    static int nibbleValue(final char c) throws SmppApiException {
        final int nibble = Character.getNumericValue(c);
        if ((nibble < 0) || (15 < nibble)) {
            throw new SmppApiException(SmppApiException.FATAL_ERROR, c + " is not a hexadecimal digit");
        }
        return nibble;
    }

    public static byte[] hexToBytes(final String hex) throws SmppApiException {
        byte[] b;
        final int len = hex.length();
        final int n = len / 2;
        if ((len & 1) == 1) { // odd length
            b = new byte[n + 1];
        } else { // even length
            b = new byte[n];
        }
        int i;
        for (i = 0; i < n; i++) {
            final int nibble0 = CommonUtils.nibbleValue(hex.charAt(2 * i));
            final int nibble1 = CommonUtils.nibbleValue(hex.charAt((2 * i) + 1));
            b[i] = (byte) ((nibble0 << 4) | nibble1);
        }
        if ((len & 1) == 1) { // odd length
            final int nibble0 = CommonUtils.nibbleValue(hex.charAt(2 * i));
            b[i] = (byte) (nibble0 << 4);
        }
        return b;
    }

    public static String convertByteStringToHex(final String data) {
        final StringBuffer b = new StringBuffer(data.length() * 2);
        for (int i = 0; i < data.length(); i++) {
            b.insert(i * 2, CommonUtils.nibbleToChar[(data.charAt(i) >> 4) & 0x0F]);
            b.insert((i * 2) + 1, CommonUtils.nibbleToChar[data.charAt(i) & 0x0F]);
        }
        return b.toString();
    }

    public static String convertByteStringToHex(final byte[] data,
                                                final int offset,
                                                final int length) {
        final StringBuilder hexString = new StringBuilder((length * 2) + 10);
        for (int i = offset; i < (offset + length); i++) {
            hexString.append(CommonUtils.nibbleToChar[(data[i] >> 4) & 0x0F]);
            hexString.append(CommonUtils.nibbleToChar[data[i] & 0x0F]);
            hexString.append(" ");
        }
        return hexString.toString();
    }

    public static String bytesToHexFormatedWithLimit(final ByteBuffer data) {
        final StringBuilder hexString = new StringBuilder((data.limit() * 2) + 10);
        for (int i = 0; i < data.limit(); i++) {
            hexString.append(CommonUtils.nibbleToChar[(data.get(i) >> 4) & 0x0F]);
            hexString.append(CommonUtils.nibbleToChar[data.get(i) & 0x0F]);
            hexString.append(" ");
        }
        return hexString.toString();
    }

    public static String bytesToHexFormated(final ByteBuffer data) {
        final StringBuffer b = new StringBuffer(data.position() * 2);
        for (int i = 0; i < data.position(); i++) {
            b.append(CommonUtils.nibbleToChar[(data.get(i) >> 4) & 0x0F]);
            b.append(CommonUtils.nibbleToChar[data.get(i) & 0x0F]);
            b.append(" ");
        }
        return b.toString();
    }

    public static String bytesToHexFormated(final ByteBuffer data,
                                            final int start,
                                            final int end) {
        if (start > end) {
            return "";
        }
        final StringBuffer b = new StringBuffer(data.position() * 2);
        for (int i = start; i < end; i++) {
            b.append(CommonUtils.nibbleToChar[(data.get(i) >> 4) & 0x0F]);
            b.append(CommonUtils.nibbleToChar[data.get(i) & 0x0F]);
            b.append(" ");
        }
        return b.toString();
    }

    public static String bytesToHexFormated(final byte[] data) {
        final StringBuffer b = new StringBuffer(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            b.append(CommonUtils.nibbleToChar[(data[i] >> 4) & 0x0F]);
            b.append(CommonUtils.nibbleToChar[data[i] & 0x0F]);
            b.append(" ");
        }
        return b.toString();
    }

    public static String bytesToHex(final byte[] data) {
        final StringBuffer b = new StringBuffer(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            b.insert(i * 2, CommonUtils.nibbleToChar[(data[i] >> 4) & 0x0F]);
            b.insert((i * 2) + 1, CommonUtils.nibbleToChar[data[i] & 0x0F]);
        }
        return b.toString();
    }

    public static String relativeTimeStringFromMinutes(final int minutes) throws Exception {
        final long miliseconds = ETime.MINUTE.getMiliseconds(minutes);
        return CommonUtils.relativeTimeStringFromMiliseconds(miliseconds);
    }

    public static String relativeTimeStringFromSeconds(final long seconds) throws Exception {
        final long miliseconds = ETime.SECOND.getMiliseconds(seconds);
        return CommonUtils.relativeTimeStringFromMiliseconds(miliseconds);
    }

    public static String relativeTimeStringFromMiliseconds(final long miliseconds) throws Exception {
        final StringBuilder strBuild = new StringBuilder(30);

        long remainingMiliseconds = miliseconds;

        ETime.YEAR.convertToString(ETime.YEAR.getUnit(remainingMiliseconds), strBuild);
        remainingMiliseconds = ETime.YEAR.getRemainingMiliseconds(remainingMiliseconds);

        ETime.MONTH.convertToString(ETime.MONTH.getUnit(remainingMiliseconds), strBuild);
        remainingMiliseconds = ETime.MONTH.getRemainingMiliseconds(remainingMiliseconds);

        ETime.DAY.convertToString(ETime.DAY.getUnit(remainingMiliseconds), strBuild);
        remainingMiliseconds = ETime.DAY.getRemainingMiliseconds(remainingMiliseconds);

        ETime.HOUR.convertToString(ETime.HOUR.getUnit(remainingMiliseconds), strBuild);
        remainingMiliseconds = ETime.HOUR.getRemainingMiliseconds(remainingMiliseconds);

        ETime.MINUTE.convertToString(ETime.MINUTE.getUnit(remainingMiliseconds), strBuild);
        remainingMiliseconds = ETime.MINUTE.getRemainingMiliseconds(remainingMiliseconds);

        ETime.SECOND.convertToString(ETime.SECOND.getUnit(remainingMiliseconds), strBuild);
        remainingMiliseconds = ETime.SECOND.getRemainingMiliseconds(remainingMiliseconds);

        strBuild.append("000R");
        return strBuild.toString();
    }

    public static long milisecondsFromRelativeString(final String relativeString) {
        final String year = relativeString.substring(0, 2);
        final String month = relativeString.substring(2, 4);
        final String day = relativeString.substring(4, 6);
        final String hour = relativeString.substring(6, 8);
        final String minute = relativeString.substring(8, 10);
        final String second = relativeString.substring(10, 12);

        final int secondInt = Integer.parseInt(second);
        final int minuteInt = Integer.parseInt(minute);
        final int hourInt = Integer.parseInt(hour);
        final int dayInt = Integer.parseInt(day);
        final int monthInt = Integer.parseInt(month);
        final int yearInt = Integer.parseInt(year);

        return ETime.YEAR.getMiliseconds(yearInt)
               + ETime.MONTH.getMiliseconds(monthInt)
               + ETime.DAY.getMiliseconds(dayInt)
               + ETime.HOUR.getMiliseconds(hourInt)
               + ETime.MINUTE.getMiliseconds(minuteInt)
               + ETime.SECOND.getMiliseconds(secondInt);
    }

    public static String getRelativeIn(final long seconds) throws Exception {
        final StringBuilder strBuild = new StringBuilder(30);
        int remainingSeconds = (int) seconds;
        if (remainingSeconds >= (518400 * 60)) {
            int year = remainingSeconds / (518400 * 60);
            remainingSeconds = remainingSeconds % (518400 * 60);
            if (year > 99) {
                year = 99;
            }
            if (year < 10) {
                strBuild.append("0");
            }
            strBuild.append(year);
        } else {
            strBuild.append("00");
        }
        if (remainingSeconds >= (43200 * 60)) {
            final int months = remainingSeconds / (43200 * 60);
            remainingSeconds = remainingSeconds % (43200 * 60);
            if (months < 10) {
                strBuild.append("0");
            }
            strBuild.append(months);
        } else {
            strBuild.append("00");
        }
        if (remainingSeconds >= (1440 * 60)) {
            final int days = remainingSeconds / (1440 * 60);
            remainingSeconds = remainingSeconds % (1440 * 60);
            if (days < 10) {
                strBuild.append("0");
            }
            strBuild.append(days);
        } else {
            strBuild.append("00");
        }
        if (remainingSeconds >= (60 * 60)) {
            final int hours = remainingSeconds / (60 * 60);
            remainingSeconds = remainingSeconds % (60 * 60);
            if (hours < 10) {
                strBuild.append("0");
            }
            strBuild.append(hours);
        } else {
            strBuild.append("00");
        }
        if (remainingSeconds >= 60) {
            final int minutes = remainingSeconds / 60;
            remainingSeconds = remainingSeconds % 60;
            if (minutes < 10) {
                strBuild.append("0");
            }
            strBuild.append(minutes);
        } else {
            strBuild.append("00");
        }
        if (remainingSeconds < 10) {
            strBuild.append("0");
        }
        strBuild.append(remainingSeconds);
        strBuild.append("000R");
        return strBuild.toString();
    }


    public static void main(final String[] args) throws Exception {

        final long getLong = 1000000;
        System.out.println(CommonUtils.getRelativeIn(getLong));
        System.out.println(CommonUtils.relativeTimeStringFromSeconds(getLong));
        final String revertStr = CommonUtils.relativeTimeStringFromSeconds(getLong);
        long delta = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            CommonUtils.getRelativeIn(getLong);
        }
        System.out.println("Delta : " + (System.currentTimeMillis() - delta));

        delta = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            CommonUtils.relativeTimeStringFromSeconds(getLong);
        }
        System.out.println("Delta : " + (System.currentTimeMillis() - delta));

        delta = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            CommonUtils.milisecondsFromRelativeString(revertStr);
        }
        System.out.println("Delta : " + (System.currentTimeMillis() - delta));

    }


    public static String getClientHostLabel(final String clientName,
                                            final String hostName) {
        if (hostName == null) {
            return "[" + clientName + "]";
        }
        return "[" + clientName + ":" + hostName + "]";
    }

    public static boolean checkStringEquality(final String str1,
                                              final String str2) {
        if ((str1 == null) && (str2 == null)) {
            return true;
        }
        if (str1 != null) {
            if (str1.equals(str2)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkStringIsEmpty(final String str) {
        if (str == null) {
            return true;
        }
        if ("".equals(str.trim())) {
            return true;
        }
        return false;
    }

    public static byte[] convertDateToWBXML(final long date) throws Exception {
        byte[] byteArr = null;
        final String dateStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(date));

        byteArr = new byte[dateStr.length() / 2];
        for (int i = 0; i < dateStr.length(); i += 2) {
            final int number = (Integer.parseInt(dateStr.substring(i, i + 1)) * 16) + Integer.parseInt(dateStr.substring(i + 1, i + 2));
            byteArr[i / 2] = (byte) number;
        }
        return byteArr;
    }

}
