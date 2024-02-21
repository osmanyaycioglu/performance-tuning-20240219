package com.adenon.api.smpp.sdk;

public enum EDataCoding {
    GSM_DEFAULT(0),
    GSM_DEFAULT_WITH_ESCAPE(0),
    TURKISH_SINGLE_SHIFT(0),
    ASCII(1),
    ISO_8859_1(3),
    WAP(4),
    UCS2(8),
    ASCII_FLASH(16),
    UCS2_FLASH(24);

    private final int value;

    private EDataCoding(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static EDataCoding getDataCoding(final int val) {
        switch (val) {
            case 0:
                return GSM_DEFAULT;
            case 1:
                return ASCII;
            case 3:
                return ISO_8859_1;
            case 8:
                return UCS2;
            case 16:
                return ASCII_FLASH;
            case 24:
                return UCS2_FLASH;
            default:
                return GSM_DEFAULT;
        }
    }

    public static EDataCoding getDataCoding(final String name) {
        if ("ascii".equalsIgnoreCase(name)) {
            return ASCII;
        }

        if ("gsm".equalsIgnoreCase(name) || "gsm7".equalsIgnoreCase(name)) {
            return GSM_DEFAULT;
        }

        if ("gsmext".equalsIgnoreCase(name) || "gsm7ext".equalsIgnoreCase(name)) {
            return GSM_DEFAULT_WITH_ESCAPE;
        }

        if ("iso8859_1".equalsIgnoreCase(name)) {
            return ISO_8859_1;
        }

        if ("utf8".equalsIgnoreCase(name) || "ucs2".equalsIgnoreCase(name) || "unicode".equalsIgnoreCase(name)) {
            return UCS2;
        }

        if ("sstr".equalsIgnoreCase(name) || "ss_tr".equalsIgnoreCase(name) || "tr_ss".equalsIgnoreCase(name) || "trss".equalsIgnoreCase(name)) {
            return TURKISH_SINGLE_SHIFT;
        }

        if ("wap".equalsIgnoreCase(name)) {
            return WAP;
        }

        return ASCII;
    }
}
