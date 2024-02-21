package com.adenon.library.common.utils;

import java.util.UUID;


public class StringUtils {

    public static boolean checkStringIsEmpty(final String str) {
        if (str == null) {
            return true;
        }
        if ("".equals(str.trim())) {
            return true;
        }
        return false;
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

    public static String generateUUID() {
        final UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString();
    }
}
