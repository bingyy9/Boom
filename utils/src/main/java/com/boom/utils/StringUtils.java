package com.boom.utils;

public class StringUtils {
    public static boolean isEmpty(final String string) {
        return (string == null || KotlinUtil.trim(string).length() == 0);
    }

    public static boolean isNotNullStringEquals(final String string1, final String string2) {
        if (isEmpty(string1) || isEmpty(string2)) {
            return false;
        }

        return KotlinUtil.trim(string1).equals(KotlinUtil.trim(string2));
    }
}
